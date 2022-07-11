package eu.mshade.enderchest.marshal.common;

import eu.mshade.enderchest.entity.DefaultRideable;
import eu.mshade.enderframe.entity.Rideable;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagMarshal;

import java.lang.reflect.Type;

public class DefaultRideableMarshal implements BinaryTagMarshal<Rideable> {

    @Override
    public BinaryTag<?> serialize(BinaryTagDriver binaryTagDriver, Rideable rideable) {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();
        compoundBinaryTag.putBoolean("hasSaddle", rideable.hasSaddle());

        return compoundBinaryTag;
    }

    @Override
    public Rideable deserialize(BinaryTagDriver binaryTagDriver, BinaryTag<?> binaryTag) {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;
        return new DefaultRideable(compoundBinaryTag.getBoolean("hasSaddle"));
    }
}
