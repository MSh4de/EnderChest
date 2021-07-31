package eu.mshade.enderchest.redstone.protocol;

import eu.mshade.enderchest.redstone.protocol.packet.RedstonePacketInMotd;
import eu.mshade.enderframe.protocol.PacketOut;
import eu.mshade.mwork.MWork;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RedstonePacketCodec extends MessageToMessageCodec<ByteBuf, RedstonePacketOut> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RedstonePacketOut msg, List<Object> out) throws Exception {
        String packet = MWork.get().getObjectMapper().writeValueAsString(msg);
        System.out.println(packet);
        out.add(Unpooled.buffer().writeBytes(packet.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        String packet = msg.toString(StandardCharsets.UTF_8);
        RedstonePacketIn redstonePacketIn = MWork.get().getObjectMapper().readValue(packet, RedstonePacketIn.class);
        out.add(redstonePacketIn);
    }
}
