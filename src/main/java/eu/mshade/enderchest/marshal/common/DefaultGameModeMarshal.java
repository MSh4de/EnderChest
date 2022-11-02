package eu.mshade.enderchest.marshal.common;

import eu.mshade.enderframe.GameMode;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagMarshal;

public class DefaultGameModeMarshal implements BinaryTagMarshal<GameMode> {

    @Override
    public BinaryTag<?> serialize(BinaryTagDriver binaryTagDriver, GameMode gameMode) {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();
        compoundBinaryTag.putString("gameMode", gameMode.toString());

        return compoundBinaryTag;
    }

    @Override
    public GameMode deserialize(BinaryTagDriver binaryTagDriver, BinaryTag<?> binaryTag) {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;
        return GameMode.valueOf(compoundBinaryTag.getString("gameMode"));
    }
}
