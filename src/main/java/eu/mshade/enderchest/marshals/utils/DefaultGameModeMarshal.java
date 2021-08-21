package eu.mshade.enderchest.marshals.utils;

import eu.mshade.enderframe.GameMode;
import eu.mshade.enderframe.entity.CreeperState;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshalBuffer;

import java.lang.reflect.Type;

public class DefaultGameModeMarshal implements BinaryTagMarshalBuffer<GameMode> {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, GameMode gameMode, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();
        compoundBinaryTag.putString("gameMode", gameMode.toString());

        return compoundBinaryTag;
    }

    @Override
    public GameMode deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;
        return GameMode.valueOf(compoundBinaryTag.getString("gameMode"));
    }
}
