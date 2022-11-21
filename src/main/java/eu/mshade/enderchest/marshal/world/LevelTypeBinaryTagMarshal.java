package eu.mshade.enderchest.marshal.world;

import eu.mshade.enderframe.world.LevelType;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.BinaryTagMarshal;
import eu.mshade.mwork.binarytag.StringBinaryTag;

public class LevelTypeBinaryTagMarshal implements BinaryTagMarshal<LevelType> {

    @Override
    public BinaryTag<?> serialize(BinaryTagDriver binaryTagDriver, LevelType levelType) {
        return new StringBinaryTag(levelType.name());
    }

    @Override
    public LevelType deserialize(BinaryTagDriver binaryTagDriver, BinaryTag<?> binaryTag) {
        return LevelType.valueOf((String) binaryTag.getValue());
    }

}
