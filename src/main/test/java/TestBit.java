import java.io.*;
import java.util.Arrays;
import java.util.BitSet;

public class TestBit {
    static byte[] array = new byte[]{32, -1, -1, -1, -39, -1, -1, -1, -23, 1, 31, -30, -10, 1, 5, 18, 0, 112, 16, -128, 2, 21, 19, -112, 9, 48, -16, 1, -48, 2, -80, 1, -112, 6, -105, 1, -106, 1, -107, 1, -108, 1, -109, 1, 17, -64, 2, 16, -124, 33, 8, 66, 16, -124, 33, 33, 8, 66, 16, -124, 33, 8, 66, 66, 16, -124, 33, 8, 66, 16, -124, -124, 33, 8, 66, 16, -124, 33, 8, 8, 66, 16, -124, 33, 8, 66, 16, 16, -124};


    public static void main(String[] args) throws IOException {




        /*
        int v = 0x00;
        int delta = 0;
        for (int i = 4; i < 8; i++) {
            v |= (getBit(111, i) != 0 ? 1 : 0);
            v = v << 1;
            if (v == 0) delta++;
            System.out.println(getBit(111, i) != 0);
        }

        System.out.println("V: "+v);

         */
        /*
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(array));

        VariableValueArray variableValueArray = new VariableValueArray(5, 4096);
        int a = 0;
        try {
            long l = dataInputStream.readLong();
            variableValueArray.getBacking()[a++] = l;
        } catch (Exception e) {

        }

        for (int i = 0; i < 50; i++) {
            System.out.println(variableValueArray.get(i));

        }

         */




        //nibbleBit.toByteArray()[1] = (byte) 128;
        //System.out.println(reverseBits(24));
        /*
        for (int i = 0; i < array.length; i++) {
            nibbleBit.toByteArray()[i] = array[i];
        }

        for (int i = 0; i < 50; i++) {
            System.out.println(reverseBits(nibbleBit.get(i)));

        }

         */


        NibbleBit nibbleBit = new NibbleBit(4096, 5);


        FlexibleStorage flexibleStorage = new FlexibleStorage(5, 4096);



        for (int i = 0; i < 8; i++) {
        }
        nibbleBit.set(0, 1);
        nibbleBit.set(1, 5);

        flexibleStorage.set(0, 1);
        flexibleStorage.set(1, 5);
        System.out.println("========================");
        /*
        for (int i = 0; i < 4096; i++) {
            System.out.println(nibbleBit.get(i));
        }

         */

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        for (long datum : flexibleStorage.getData()) {
            dataOutputStream.writeLong(datum);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            stringBuilder.append(byteArrayOutputStream.toByteArray()[i]);
            if (i != 8-1) stringBuilder.append(" ,");
        }
        System.out.println(stringBuilder.toString());



        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        System.out.println(dataInputStream.readLong());
        System.out.println(nibbleBit.toByteArray()[0] +", "+ nibbleBit.toByteArray()[1] +", "+ nibbleBit.toByteArray()[2] +", "+ nibbleBit.toByteArray()[3]);
        System.out.println(Long.BYTES*8);

        System.out.println("========================");
       // System.out.println(Long.toBinaryString((long) flexibleStorage.getData()[0]));
        System.out.println(nibbleBit.get(0));
        System.out.println(nibbleBit.get(1));
       // System.out.println(flexibleStorage.get(1));


    }

    public static int getBit(int v, int p){
        int mask = (1 << p) ;
        return  (v & mask);
    }

    static int reverseBits(int num) {
        int b = 0;
        while (num != 0) {
            b <<= 1;
            b |= (num & 1);
            num >>= 1;
        }
        return b;
    }
}
