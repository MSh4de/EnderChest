package eu.mshadeproduction.enderchest.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class VarIntFrameDecoder extends ByteToMessageDecoder {

    private final Logger logger = LoggerFactory.getLogger(VarIntFrameDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (!ctx.channel().isActive()) {
            in.clear();
            return;
        }

        VarIntByteDecoder reader = new VarIntByteDecoder();
        int varIntEnd = in.forEachByte(reader);

        if (varIntEnd == -1) return;

        if (reader.getResult() == DecodeResult.SUCCESS) {
            int readVarInt = reader.getReadVarInt();
            int bytesRead = reader.getBytesRead();
            if (readVarInt < 0) {
                logger.error("[VarIntFrameDecoder] Bad data length");
            } else if (readVarInt == 0) {
                in.readerIndex(varIntEnd + 1);
            } else {
                int minimumRead = bytesRead + readVarInt;
                if (in.isReadable(minimumRead)) {
                    out.add(in.retainedSlice(varIntEnd + 1, readVarInt));
                    in.skipBytes(minimumRead);
                }
            }
        } else if (reader.getResult() == DecodeResult.TOO_BIG) {
            logger.error("[VarIntFrameDecoder] Too big data");
        }
    }
}