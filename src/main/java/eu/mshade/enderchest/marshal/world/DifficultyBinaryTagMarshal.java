package eu.mshade.enderchest.marshal.world;

import eu.mshade.enderframe.world.Difficulty;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.entity.StringBinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagMarshal;

import java.lang.reflect.Type;

public class DifficultyBinaryTagMarshal implements BinaryTagMarshal<Difficulty> {


    @Override
    public BinaryTag<?> serialize(BinaryTagDriver binaryTagDriver, Difficulty difficulty) throws Exception {
        return new StringBinaryTag(difficulty.name());
    }

    @Override
    public Difficulty deserialize(BinaryTagDriver binaryTagDriver, BinaryTag<?> binaryTag) throws Exception {
        return Difficulty.valueOf((String) binaryTag.getValue());
    }
}
