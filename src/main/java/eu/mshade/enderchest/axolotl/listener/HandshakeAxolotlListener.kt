package eu.mshade.enderchest.axolotl.listener

import eu.mshade.axolotl.Axolotl
import eu.mshade.axolotl.event.HandshakeAxolotlEvent
import eu.mshade.axolotl.protocol.AxolotlProtocolPipeline
import eu.mshade.axolotl.protocol.AxolotlProtocolRepository
import eu.mshade.enderchest.EnderChest
import eu.mshade.enderchest.axolotl.AxololtConnection
import eu.mshade.enderframe.world.WorldRepository
import eu.mshade.mwork.event.EventListener
import java.util.function.Consumer

class HandshakeAxolotlListener : EventListener<HandshakeAxolotlEvent> {

    override fun onEvent(event: HandshakeAxolotlEvent) {
        val axolotlProtocol = AxolotlProtocolRepository.getProtocol(event.axolotlProtocolVersion)

        if (axolotlProtocol == null) {
            event.axolotlSession.channel.close()
            return
        }

        AxolotlProtocolPipeline.protocolByChannel[event.axolotlSession.channel] = axolotlProtocol
        AxolotlProtocolPipeline.axolotlSessionByChannel[event.axolotlSession.channel] = axolotlProtocol.getAxolotlSession(event.axolotlSession.channel)
        AxololtConnection.connections.add(event.axolotlSession.channel)

        val axolotlSession = AxolotlProtocolPipeline.axolotlSessionByChannel[event.axolotlSession.channel]!!

        WorldRepository.getWorlds().forEach {
            axolotlSession.sendInitializeWorld(it)
        }
        try{
            EnderChest.players.forEach {
                println("Sending player ${it.name} to ${event.axolotlSession.channel}")
                axolotlSession.sendInitializePlayer(it)
            }
        }catch (throwable: Throwable){
            throwable.printStackTrace()
        }
    }
}