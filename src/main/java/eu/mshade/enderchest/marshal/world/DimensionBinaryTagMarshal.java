package eu.mshade.enderchest.marshal.world;

import eu.mshade.enderframe.world.Dimension;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.BinaryTagMarshal;
import eu.mshade.mwork.binarytag.StringBinaryTag;

public class DimensionBinaryTagMarshal implements BinaryTagMarshal<Dimension> {

    @Override
    public BinaryTag<?> serialize(BinaryTagDriver binaryTagDriver, Dimension dimension) {
        return new StringBinaryTag(dimension.name());
    }

    @Override
    public Dimension deserialize(BinaryTagDriver binaryTagDriver, BinaryTag<?> binaryTag) {
        return Dimension.valueOf((String) binaryTag.getValue());
    }
}
