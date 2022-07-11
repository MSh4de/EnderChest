package eu.mshade.enderchest.protocol;

import eu.mshade.enderframe.protocol.Protocol;
import eu.mshade.enderframe.protocol.temp.TempProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

public class PacketAccuracy extends ByteToMessageCodec<ByteBuf> {

    private Protocol protocol = TempProtocol.getInstance();

    private static boolean readableVarInt(ByteBuf buf) {
        if (buf.readableBytes() > 5) {
            // maximum varint size
            return true;
        }

        int idx = buf.readerIndex();
        byte in;
        do {
            if (buf.readableBytes() < 1) {
                buf.readerIndex(idx);
                return false;
            }
            in = buf.readByte();
        } while ((in & 0x80) != 0);

        buf.readerIndex(idx);
        return true;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        protocol.getProtocolBuffer(out).writeVarInt(msg.readableBytes());
        out.writeBytes(msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
            throws Exception {
        // check for length field readability
        in.markReaderIndex();
        if (!readableVarInt(in)) {
            return;
        }

        // check for contents readability
        int length = protocol.getProtocolBuffer(in).readVarInt();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }

        // read contents into buf
        ByteBuf buf = ctx.alloc().buffer(length);
        in.readBytes(buf, length);
        out.add(buf);
    }
}
