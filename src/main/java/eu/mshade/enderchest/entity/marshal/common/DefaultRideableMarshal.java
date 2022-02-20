package eu.mshade.enderchest.entity.marshal.common;

import eu.mshade.enderchest.entity.DefaultRideable;
import eu.mshade.enderframe.entity.Rideable;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshalBuffer;

import java.lang.reflect.Type;

public class DefaultRideableMarshal implements BinaryTagMarshalBuffer<Rideable> {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, Rideable rideable, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();
        compoundBinaryTag.putBoolean("hasSaddle", rideable.hasSaddle());

        return compoundBinaryTag;
    }

    @Override
    public Rideable deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;
        return new DefaultRideable(compoundBinaryTag.getBoolean("hasSaddle"));
    }
}
