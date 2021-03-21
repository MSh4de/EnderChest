package eu.mshadeproduction.enderchest.protocol;

import io.netty.util.ByteProcessor;

public class VarIntByteDecoder implements ByteProcessor {

    private int readVarInt;
    private int bytesRead;
    private DecodeResult result = DecodeResult.TOO_SHORT;

    @Override
    public boolean process(byte k) {
        readVarInt |= (k & 0x7F) << bytesRead++ * 7;
        if (bytesRead > 3) {
            result = DecodeResult.TOO_BIG;
            return false;
        }
        if ((k & 0x80) != 128) {
            result = DecodeResult.SUCCESS;
            return false;
        }
        return true;
    }

    public int getReadVarInt() {
        return readVarInt;
    }

    public int getBytesRead() {
        return bytesRead;
    }

    public DecodeResult getResult() {
        return result;
    }
}