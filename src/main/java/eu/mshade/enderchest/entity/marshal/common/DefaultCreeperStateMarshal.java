package eu.mshade.enderchest.entity.marshal.common;

import eu.mshade.enderframe.entity.CreeperState;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshalBuffer;

import java.lang.reflect.Type;

public class DefaultCreeperStateMarshal implements BinaryTagMarshalBuffer<CreeperState> {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, CreeperState creeperState, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();
        compoundBinaryTag.putString("creeperState", creeperState.toString());

        return compoundBinaryTag;
    }

    @Override
    public CreeperState deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;
        return CreeperState.valueOf(compoundBinaryTag.getString("creeperState"));
    }
}
