package eu.mshade.enderchest.marshal.common;

import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagMarshal;

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
