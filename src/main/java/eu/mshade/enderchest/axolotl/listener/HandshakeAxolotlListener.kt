package eu.mshade.enderchest.axolotl.listener

import eu.mshade.axolotl.event.HandshakeAxolotlEvent
import eu.mshade.axolotl.protocol.AxolotlProtocolPipeline
import eu.mshade.axolotl.protocol.AxolotlProtocolRepository
import eu.mshade.enderchest.EnderChest
import eu.mshade.enderchest.axolotl.AxololtConnection
import eu.mshade.enderframe.world.WorldRepository
import eu.mshade.mwork.event.EventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class HandshakeAxolotlListener : EventListener<HandshakeAxolotlEvent> {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(HandshakeAxolotlListener::class.java)
    }

    override fun onEvent(event: HandshakeAxolotlEvent) {
        val axolotlProtocol = AxolotlProtocolRepository.getProtocol(event.axolotlProtocolVersion)

        if (axolotlProtocol == null) {
            event.axolotlSession.channel.close()
            return
        }

        AxolotlProtocolPipeline.protocolByChannel[event.axolotlSession.channel] = axolotlProtocol
        val axolotlSession = axolotlProtocol.getAxolotlSession(event.axolotlSession.channel)
        AxolotlProtocolPipeline.axolotlSessionByChannel[event.axolotlSession.channel] = axolotlSession
        AxololtConnection.connections.add(event.axolotlSession.channel)

        LOGGER.info("New connection to Axolotl: ${event.axolotlSession.channel.remoteAddress()} with session $axolotlSession")

        try{

            runBlocking(Dispatchers.IO) {
                axolotlSession.sendInitialization(WorldRepository.getWorlds(), EnderChest.players)
            }
        }catch (throwable: Throwable){
            throwable.printStackTrace()
        }

    }
}