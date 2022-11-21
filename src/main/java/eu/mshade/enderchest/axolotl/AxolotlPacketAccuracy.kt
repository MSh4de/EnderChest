package eu.mshade.enderchest.axolotl

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec

class AxolotlPacketAccuracy: ByteToMessageCodec<ByteBuf>() {

    private fun readableInt(buf: ByteBuf): Boolean{
        return buf.readableBytes() >= 4
    }


    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf) {
        out.writeInt(msg.readableBytes())
        out.writeBytes(msg)
    }

    override fun decode(ctx: ChannelHandlerContext, `in`: ByteBuf, out: MutableList<Any?>) {
        // check for length field readability
        `in`.markReaderIndex()
        if (!readableInt(`in`)) {
            return
        }
        // check for contents readability
        val length: Int = `in`.readInt()
        if (`in`.readableBytes() < length) {
            `in`.resetReaderIndex()
            return
        }

        // read contents into buf
        val buf = ctx.alloc().buffer(length)
        `in`.readBytes(buf, length)
        out.add(buf)
    }

}