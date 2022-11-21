package eu.mshade.enderchest.axolotl

import eu.mshade.axolotl.Axolotl
import eu.mshade.axolotl.protocol.AxolotlPacketIn
import eu.mshade.axolotl.protocol.AxolotlProtocolPipeline
import eu.mshade.axolotl.protocol.AxolotlProtocolRepository
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class AxolotlChannelInboundHandlerAdapter: ChannelInboundHandlerAdapter() {

    override fun channelActive(ctx: ChannelHandlerContext) {
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        AxololtConnection.connections.remove(ctx.channel())
        AxolotlProtocolPipeline.flush(ctx.channel())
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg is AxolotlPacketIn) {
            AxolotlProtocolPipeline.protocolByChannel[ctx.channel()]!!.eventBus.publish(msg)
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {

    }
}