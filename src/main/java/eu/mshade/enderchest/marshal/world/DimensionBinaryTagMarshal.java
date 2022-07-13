package eu.mshade.enderchest.marshal.world;

import eu.mshade.enderframe.world.Dimension;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.entity.StringBinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagMarshal;

import java.lang.reflect.Type;

public class DimensionBinaryTagMarshal implements BinaryTagMarshal<Dimension> {

    @Override
    public BinaryTag<?> serialize(BinaryTagDriver binaryTagDriver, Dimension dimension) throws Exception {
        return new StringBinaryTag(dimension.name());
    }

    @Override
    public Dimension deserialize(BinaryTagDriver binaryTagDriver, BinaryTag<?> binaryTag) throws Exception {
        return Dimension.valueOf((String) binaryTag.getValue());
    }
}
