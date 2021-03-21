package eu.mshadeproduction.enderchest.protocol;

import eu.mshadeproduction.enderframe.DefaultByteMessage;
import eu.mshadeproduction.enderframe.protocol.ByteMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;

import java.util.List;
import java.util.zip.Inflater;

public class PacketDecompress extends ByteToMessageDecoder {

    private final Inflater inflater = new Inflater();
    private int treshold;

    public PacketDecompress(int treshold) {
        this.treshold = treshold;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() != 0) {
            ByteMessage byteMessage = new DefaultByteMessage(byteBuf);
            int i = byteMessage.readVarInt();

            if (i == 0) {
                list.add(byteMessage.readBytes(byteMessage.readableBytes()));
            } else {
                if (i < treshold) {
                    throw new DecoderException("Badly compressed packet - size of " + i + " is below server threshold of " + this.treshold);
                }

                if (i > 2097152) {
                    throw new DecoderException("Badly compressed packet - size of " + i + " is larger than protocol maximum of " + 2097152);
                }

                byte[] abyte = new byte[byteMessage.readableBytes()];

                byteMessage.readBytes(abyte);
                this.inflater.setInput(abyte);
                byte[] abyte1 = new byte[i];

                this.inflater.inflate(abyte1);
                list.add(Unpooled.wrappedBuffer(abyte1));
                this.inflater.reset();
            }

        }
    }
}
