package eu.mshadeproduction.enderchest.protocol;

import eu.mshadeproduction.enderframe.DefaultByteMessage;
import eu.mshadeproduction.enderframe.protocol.ByteMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.zip.Deflater;

public class PacketCompress extends MessageToByteEncoder<ByteBuf> {

    private final byte[] buffer = new byte[8192];
    private final Deflater deflater = new Deflater();
    private int treshold;

    public PacketCompress(int treshold) {
        this.treshold = treshold;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        int i = msg.readableBytes();
        ByteMessage byteMessage = new DefaultByteMessage(out);

        if (i < treshold) {
            byteMessage.writeVarInt(0);
            byteMessage.writeBytes(msg);
        } else {
            byte[] abyte = new byte[i];
            msg.readBytes(abyte);
            byteMessage.writeVarInt(abyte.length);
            this.deflater.setInput(abyte, 0, i);
            this.deflater.finish();

            while (!this.deflater.finished()) {
                int j = this.deflater.deflate(this.buffer);

                byteMessage.writeBytes(this.buffer, 0, j);
            }

            this.deflater.reset();
        }
    }
}
