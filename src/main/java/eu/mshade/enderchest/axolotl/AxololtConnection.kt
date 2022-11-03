package eu.mshade.enderchest.axolotl

import eu.mshade.axolotl.protocol.AxolotlPacketOut
import io.netty.channel.Channel

object AxololtConnection {

    val connections = mutableListOf<Channel>()

    fun send(packet: AxolotlPacketOut) {
        connections.forEach {
            it.writeAndFlush(packet)
        }
    }

    fun send(channel: Channel, packet: AxolotlPacketOut) {
        channel.writeAndFlush(packet)
    }

}