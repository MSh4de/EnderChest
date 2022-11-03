package eu.mshade.enderchest.axolotl

import eu.mshade.axolotl.Axolotl
import eu.mshade.axolotl.protocol.AxolotlPacketOut
import eu.mshade.axolotl.protocol.AxolotlPacketType
import eu.mshade.axolotl.protocol.PacketKey
import eu.mshade.enderframe.EnderFrame
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec

class AxolotlPacketCodec: MessageToMessageCodec<ByteBuf, AxolotlPacketOut>() {

    override fun encode(channelHandlerContext: ChannelHandlerContext, axolotlPacketOut: AxolotlPacketOut, out: MutableList<Any>) {
        val buffer: ByteBuf = channelHandlerContext.alloc().buffer()

        val compoundBinaryTag = CompoundBinaryTag()
        axolotlPacketOut.write(compoundBinaryTag)
        compoundBinaryTag.putInt("packetId", Axolotl.protocolRepository.getPacketKey(axolotlPacketOut::class).getIdentifier())

        EnderFrame.get().binaryTagDriver.writeCompoundBinaryTag(compoundBinaryTag, ByteBufOutputStream(buffer))
        out.add(buffer)
    }

    override fun decode(channelHandlerContext: ChannelHandlerContext, byteBuf: ByteBuf, out: MutableList<Any>) {
        val compoundBinaryTag = EnderFrame.get().binaryTagDriver.readCompoundBinaryTag(ByteBufInputStream(byteBuf))
        val packetId = compoundBinaryTag.getInt("packetId")
        val packet = Axolotl.protocolRepository.createPacketIn(AxolotlPacketType.from(packetId))
        packet.read(compoundBinaryTag)
        out.add(packet)
    }
}