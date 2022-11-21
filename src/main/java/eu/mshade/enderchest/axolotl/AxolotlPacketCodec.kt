package eu.mshade.enderchest.axolotl

import eu.mshade.axolotl.Axolotl
import eu.mshade.axolotl.protocol.AxolotlPacketOut
import eu.mshade.axolotl.protocol.AxolotlProtocolPipeline
import eu.mshade.enderframe.EnderFrame
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec

class AxolotlPacketCodec: MessageToMessageCodec<ByteBuf, AxolotlPacketOut>() {

    override fun encode(channelHandlerContext: ChannelHandlerContext, axolotlPacketOut: AxolotlPacketOut, out: MutableList<Any>) {
        val buffer: ByteBuf = channelHandlerContext.alloc().buffer()

        val compoundBinaryTag = CompoundBinaryTag()

        val axolotlProtocol = AxolotlProtocolPipeline.protocolByChannel[channelHandlerContext.channel()]

        axolotlPacketOut.write(compoundBinaryTag)
        compoundBinaryTag.putInt("packetId", axolotlProtocol!!.packetRegistry.getPacketKey(axolotlPacketOut::class))

        EnderFrame.get().binaryTagDriver.writeCompoundBinaryTag(compoundBinaryTag, ByteBufOutputStream(buffer))
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