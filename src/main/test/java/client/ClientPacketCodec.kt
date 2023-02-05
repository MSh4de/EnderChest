package client

import eu.mshade.axolotl.Axolotl
import eu.mshade.axolotl.protocol.AxolotlPacketOut
import eu.mshade.enderframe.EnderFrame
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.MessageToMessageCodec

class ClientPacketCodec: MessageToMessageCodec<ByteBuf, CompoundBinaryTag>() {

    override fun encode(channelHandlerContext: ChannelHandlerContext, compoundBinaryTag: CompoundBinaryTag, out: MutableList<Any>) {
        val buffer: ByteBuf = channelHandlerContext.alloc().buffer()

        EnderFrame.get().binaryTagDriver.writeCompoundBinaryTag(compoundBinaryTag, ByteBufOutputStream(buffer))
        out.add(buffer)
    }

    override fun decode(channelHandlerContext: ChannelHandlerContext, byteBuf: ByteBuf, out: MutableList<Any>) {
        val compoundBinaryTag = EnderFrame.get().binaryTagDriver.readCompoundBinaryTag(ByteBufInputStream(byteBuf))
        out.add(compoundBinaryTag)
    }

}

class ClientChannelInboundHandlerAdapter: ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if(msg is CompoundBinaryTag){
            println(msg.toPrettyString())
        }
    }
}