package eu.mshade.enderchest.marshal.world;

import eu.mshade.enderframe.world.Difficulty;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.BinaryTagMarshal;
import eu.mshade.mwork.binarytag.StringBinaryTag;

public class DifficultyBinaryTagMarshal implements BinaryTagMarshal<Difficulty> {


    @Override
    public BinaryTag<?> serialize(BinaryTagDriver binaryTagDriver, Difficulty difficulty) {
        return new StringBinaryTag(difficulty.name());
    }

    @Override
    public Difficulty deserialize(BinaryTagDriver binaryTagDriver, BinaryTag<?> binaryTag) {
        return Difficulty.valueOf((String) binaryTag.getValue());
    }
}
