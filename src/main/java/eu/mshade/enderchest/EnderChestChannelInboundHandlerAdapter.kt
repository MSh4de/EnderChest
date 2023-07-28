package eu.mshade.enderchest

import eu.mshade.enderframe.EnderFrame
import eu.mshade.enderframe.MinecraftServer
import eu.mshade.enderframe.event.PlayerDisconnectEvent
import eu.mshade.enderframe.protocol.MinecraftPacketIn
import eu.mshade.enderframe.protocol.MinecraftProtocolPipeline
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class EnderChestChannelInboundHandlerAdapter(private val channel: Channel) : ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg is MinecraftPacketIn) {
            val minecraftProtocolPipeline = MinecraftProtocolPipeline.get()
            minecraftProtocolPipeline.getProtocol(channel).eventBus.publish(msg)
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        val minecraftProtocolPipeline = MinecraftProtocolPipeline.get()
        if (minecraftProtocolPipeline.getPlayer(channel) != null) {
            MinecraftServer.getMinecraftEvent().publish(PlayerDisconnectEvent(minecraftProtocolPipeline.getMinecraftSession(channel))
            )
        }
        minecraftProtocolPipeline.flush(channel)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
    }
}
