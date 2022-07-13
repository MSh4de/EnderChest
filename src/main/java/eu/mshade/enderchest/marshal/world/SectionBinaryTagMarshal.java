package eu.mshade.enderchest.marshal.world;

import eu.mshade.enderchest.world.DefaultSection;
import eu.mshade.enderframe.world.Chunk;
import eu.mshade.enderframe.world.NibbleArray;
import eu.mshade.enderframe.world.Section;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDynamicMarshal;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;

public class SectionBinaryTagMarshal implements BinaryTagDynamicMarshal {

    public BinaryTag<?> serialize(Section section) {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();

        compoundBinaryTag.putInt("y", section.getY());
        compoundBinaryTag.putInt("realBlock", section.getRealBlock());
        compoundBinaryTag.putIntArray("blocks", section.getBlocks());
        compoundBinaryTag.putByteArray("data", section.getData().getRawData());
        compoundBinaryTag.putByteArray("blockLight", section.getBlockLight().getRawData());
        compoundBinaryTag.putByteArray("skyLight", section.getSkyLight().getRawData());

        return compoundBinaryTag;
    }

    public Section deserialize(BinaryTag<?> binaryTag, Chunk chunk) {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;

        return new DefaultSection(chunk,
                compoundBinaryTag.getInt("y"),
                compoundBinaryTag.getInt("realBlock"),
                compoundBinaryTag.getIntArray("blocks"),
                new NibbleArray(compoundBinaryTag.getByteArray("data")),
                new NibbleArray(compoundBinaryTag.getByteArray("blockLight")),
                new NibbleArray(compoundBinaryTag.getByteArray("skyLight")));
    }
}
