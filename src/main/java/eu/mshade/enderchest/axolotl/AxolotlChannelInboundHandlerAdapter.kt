package eu.mshade.enderchest.axolotl

import eu.mshade.axolotl.Axolotl
import eu.mshade.axolotl.protocol.AxolotlPacketIn
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class AxolotlChannelInboundHandlerAdapter: ChannelInboundHandlerAdapter() {

    override fun channelActive(ctx: ChannelHandlerContext) {
        AxololtConnection.connections.add(ctx.channel())
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        AxololtConnection.connections.remove(ctx.channel())
    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        if (msg is AxolotlPacketIn) {
            Axolotl.eventBus.publish(msg)
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {

    }
}