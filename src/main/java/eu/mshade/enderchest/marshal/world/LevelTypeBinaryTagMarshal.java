package eu.mshade.enderchest.marshal.world;

import eu.mshade.enderframe.world.LevelType;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.entity.StringBinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagMarshal;

import java.lang.reflect.Type;

public class LevelTypeBinaryTagMarshal implements BinaryTagMarshal<LevelType> {

    @Override
    public BinaryTag<?> serialize(BinaryTagDriver binaryTagDriver, LevelType levelType) throws Exception {
        return new StringBinaryTag(levelType.name());
    }

    @Override
    public LevelType deserialize(BinaryTagDriver binaryTagDriver, BinaryTag<?> binaryTag) throws Exception {
        return LevelType.valueOf((String) binaryTag.getValue());
    }

}
