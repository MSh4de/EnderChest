package eu.mshade.enderchest

import eu.mshade.enderframe.MinecraftServer
import eu.mshade.enderframe.event.PlayerDisconnectEvent
import eu.mshade.enderframe.protocol.MinecraftPacketIn
import eu.mshade.enderframe.protocol.MinecraftProtocolPipeline
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class EnderChestChannelHandler(private val channel: Channel) : ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg is MinecraftPacketIn) {
            val minecraftProtocolPipeline = MinecraftProtocolPipeline.get()
            minecraftProtocolPipeline.getProtocol(channel).eventBus.publish(msg)
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        val minecraftProtocolPipeline = MinecraftProtocolPipeline.get()
        val player = minecraftProtocolPipeline.getPlayer(channel)
        if (player != null) {
            MinecraftServer.getMinecraftEvent().publish(PlayerDisconnectEvent(player)
            )
        }
        minecraftProtocolPipeline.flush(channel)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
    }
}
