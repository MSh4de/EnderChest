package eu.mshade.enderchest.axolotl

import eu.mshade.axolotl.protocol.AxolotlPacketOut
import eu.mshade.axolotl.protocol.AxolotlProtocolPipeline
import eu.mshade.axolotl.protocol.AxolotlSession
import io.netty.channel.Channel
import java.util.function.Consumer

object AxololtConnection {

    val connections = mutableListOf<Channel>()

    fun send(axolotlSession: Consumer<AxolotlSession>) {
        connections.forEach {
            axolotlSession.accept(AxolotlProtocolPipeline.axolotlSessionByChannel[it]!!)
        }
    }

    fun send(channel: Channel, packet: AxolotlPacketOut) {
        channel.writeAndFlush(packet)
    }

}