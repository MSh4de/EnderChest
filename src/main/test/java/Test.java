import eu.mshade.enderframe.item.MaterialKey;
import eu.mshade.enderman.wrapper.EndermanMaterialKeyWrapper;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class Test {


    public static void main(String[] args) throws InterruptedException, IOException {
        BinaryTagDriver binaryTagDriver = new BinaryTagDriver();
        GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream("C:/Users/reala/Downloads/spawn-e1930.schematic"));
        CompoundBinaryTag compoundBinaryTag = binaryTagDriver.readCompoundBinaryTag(new ByteArrayInputStream(gzipInputStream.readAllBytes()));
        System.out.println(compoundBinaryTag.getValue().keySet());
        System.out.println(compoundBinaryTag.getBinaryTag("Platform"));
        System.out.println(compoundBinaryTag.getBinaryTag("Materials"));
        byte[] blocks = compoundBinaryTag.getByteArray("Blocks");
        byte[] data = compoundBinaryTag.getByteArray("Data");

        EndermanMaterialKeyWrapper endermanMaterialKeyWrapper = new EndermanMaterialKeyWrapper();

        int unknown = 0;
        for (int i = 0; i < blocks.length; i++) {
            byte block = blocks[i];
            MaterialKey materialKey = MaterialKey.from(block);
            MaterialKey reverse = endermanMaterialKeyWrapper.reverseMap(materialKey);
            if (reverse == null){
                unknown++;
                System.out.println("Unknown block: " + materialKey);
            }
        }

        double percentage = (double)unknown / (double)blocks.length;

        System.out.println(percentage * 100 + "% unknown");

        System.out.println(Integer.MAX_VALUE);
        System.out.println((1 << 31));
    }

    public static int getIndex(int x, int y, int z){
        return ((y & 0xf) << 8) | (z << 4) | x;
    }


}
