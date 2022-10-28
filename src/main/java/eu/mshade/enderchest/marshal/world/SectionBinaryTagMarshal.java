package eu.mshade.enderchest.marshal.world;

import eu.mshade.enderchest.marshal.metadata.MetadataKeyValueBinaryTagMarshal;
import eu.mshade.enderchest.world.DefaultSection;
import eu.mshade.enderframe.UniqueId;
import eu.mshade.enderframe.item.Material;
import eu.mshade.enderframe.item.MaterialKey;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.enderframe.world.chunk.NibbleArray;
import eu.mshade.enderframe.world.chunk.Palette;
import eu.mshade.enderframe.world.chunk.Section;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.BinaryTagDynamicMarshal;
import eu.mshade.mwork.binarytag.BinaryTagType;
import eu.mshade.mwork.binarytag.entity.ArrayBinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.entity.ListBinaryTag;

public class SectionBinaryTagMarshal implements BinaryTagDynamicMarshal {

    private final PaletteBinaryTagMarshal paletteBinaryTagMarshal;
    private final UniqueIdBinaryTagMarshal uniqueIdBinaryTagMarshal;

    public SectionBinaryTagMarshal(BinaryTagDriver binaryTagDriver) {
        this.paletteBinaryTagMarshal = binaryTagDriver.getDynamicMarshal(PaletteBinaryTagMarshal.class);
        this.uniqueIdBinaryTagMarshal = binaryTagDriver.getDynamicMarshal(UniqueIdBinaryTagMarshal.class);
    }

    public BinaryTag<?> serialize(Section section) {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();

        compoundBinaryTag.putInt("y", section.getY());
        compoundBinaryTag.putInt("realBlock", section.getRealBlock());
        compoundBinaryTag.putBinaryTag("uniqueId", uniqueIdBinaryTagMarshal.serialize(section.getUniqueId()));

        compoundBinaryTag.putBinaryTag("palette", paletteBinaryTagMarshal.serialize(section.getPalette()));

        compoundBinaryTag.putIntArray("blocks", section.getBlocks());
        compoundBinaryTag.putByteArray("blockLight", section.getBlockLight().getRawData());
        compoundBinaryTag.putByteArray("skyLight", section.getSkyLight().getRawData());

        return compoundBinaryTag;
    }

    public Section deserialize(BinaryTag<?> binaryTag, Chunk chunk) {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;

        int y = compoundBinaryTag.getInt("y");
        int realBlock = compoundBinaryTag.getInt("realBlock");
        UniqueId uniqueId = uniqueIdBinaryTagMarshal.deserialize(compoundBinaryTag.getBinaryTag("uniqueId"));
        Palette palette = paletteBinaryTagMarshal.deserialize(compoundBinaryTag.getBinaryTag("palette"));
        int[] blocks = compoundBinaryTag.getIntArray("blocks");
        NibbleArray blockLight = new NibbleArray(compoundBinaryTag.getByteArray("blockLight"));
        NibbleArray skyLight = new NibbleArray(compoundBinaryTag.getByteArray("skyLight"));
        return new DefaultSection(chunk, y, realBlock, palette, blocks, uniqueId, blockLight, skyLight);
    }
}
