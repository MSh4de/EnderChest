import java.util.Arrays;

public class NibbleBit {

    private byte[] payload;
    private int bitsPerData;
    private int maxBitPerData;

    public NibbleBit(int size, int bitsPerData) {
        this.bitsPerData = bitsPerData;
        this.maxBitPerData = (1 << bitsPerData) - 1;
        this.payload = new byte[(size * bitsPerData) / 8];
    }

    public void set(int index, int v) {
        v &= maxBitPerData;
        int half = (index * bitsPerData) / 8;
        int offSetBit = 0;
        while (offSetBit <= (index * bitsPerData) + bitsPerData) {
            offSetBit += 8;
        }
        int startBit = (index * bitsPerData);
        int endBit = startBit + bitsPerData;
        int b = (payload[half] & 0xff);
        int b2 = 0;
        long bit = 0;

        for (int i = half*8; i <= offSetBit; i += 8) {
            int pointer = i / 8;
            boolean outBound = pointer > (payload.length - 1);
            if (!outBound){
                bit |= ((long) (payload[pointer] & 0xff) << (offSetBit-i));
            }
        }

        int shift = offSetBit - (endBit);
        long r = bit >> shift | v;
        long merge = (bit & ((1L << shift) - 1)) | (r << shift);

        System.out.println(Long.toBinaryString(bit));
        System.out.println(Long.toBinaryString(merge));

        int pointer = half;
        int power = offSetBit  - (half*8);
        for (int i = offSetBit-8; i >= half*8; i -= 8) {
            //int pointer = i / 8;
            boolean outBound = pointer > (payload.length - 1);
            if (!outBound){
                payload[pointer++] = (byte) (merge >>> (power -= 8));
            }
        }

        System.out.println(bit);

        /*
        boolean outBound = half < (payload.length - 1);

        if(outBound) b2 = payload[(half + 1)] & 0xff;

        int shift = Math.abs(16 - (startBit % 8 + bitsPerData));

        int bit = b << 8 | b2;

        int i = bit >> shift | v;
        int merge = (bit & (1 << shift) - 1) | (i << shift);

        payload[half] = (byte) ((merge >> 8));
        if (outBound) payload[half + 1] = (byte) (merge & ((1 << 8) - 1));

         */

    }

    public int get(int index) {
        int half = (index * bitsPerData) / 8;
        int offSetBit = 0;
        while (offSetBit <= index * bitsPerData) {
            offSetBit += 8;
        }
        int startBit = (index * bitsPerData);
        int b = payload[half] & 0xff;
        boolean outBound = half < (payload.length - 1);
        int b2 = 0;
        if (outBound) {
           b2 = payload[(half + 1)] & 0xff;
        }
        int bit = b << 8 | b2;
        System.out.println(Long.toBinaryString(bit));

        int shift = Math.abs(16 - ((startBit % 8) + bitsPerData));

        return (bit >> shift) & (maxBitPerData);
    }

    public byte[] toByteArray() {
        return payload;
    }
}
