package eu.mshade.enderchest.axolotl

import eu.mshade.axolotl.protocol.AxolotlPacketOut
import eu.mshade.axolotl.protocol.AxolotlProtocolPipeline
import eu.mshade.enderframe.EnderFrame
import eu.mshade.mwork.binarytag.DeflateCompoundBinaryTag
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import java.io.ByteArrayOutputStream

class AxolotlPacketCodec: MessageToMessageCodec<ByteBuf, AxolotlPacketOut>() {

    override fun encode(channelHandlerContext: ChannelHandlerContext, axolotlPacketOut: AxolotlPacketOut, out: MutableList<Any>) {

        val byteArrayOutputStream = ByteArrayOutputStream()
        val compoundBinaryTag = DeflateCompoundBinaryTag()
        val axolotlProtocol = AxolotlProtocolPipeline.protocolByChannel[channelHandlerContext.channel()]

        axolotlPacketOut.write(compoundBinaryTag)
        compoundBinaryTag.putInt("packetId", axolotlProtocol!!.packetRegistry.getPacketKey(axolotlPacketOut::class))

        EnderFrame.get().binaryTagDriver.writeCompoundBinaryTag(compoundBinaryTag, byteArrayOutputStream)
        val buffer: ByteBuf = channelHandlerContext.alloc().buffer(byteArrayOutputStream.size())
        buffer.writeBytes(byteArrayOutputStream.toByteArray())
        out.add(buffer)
    }

    override fun decode(channelHandlerContext: ChannelHandlerContext, byteBuf: ByteBuf, out: MutableList<Any>) {
        val compoundBinaryTag = EnderFrame.get().binaryTagDriver.readCompoundBinaryTag(ByteBufInputStream(byteBuf))
        val packetId = compoundBinaryTag.getInt("packetId")

        val axolotlProtocol = AxolotlProtocolPipeline.protocolByChannel[channelHandlerContext.channel()]
        val axolotlSession = AxolotlProtocolPipeline.axolotlSessionByChannel[channelHandlerContext.channel()]

        val packet = axolotlProtocol!!.packetRegistry.createPacketIn(packetId)
        packet.read(axolotlSession!!, compoundBinaryTag)
        out.add(packet)
    }
}