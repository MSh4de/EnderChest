package eu.mshade.enderchest.marshal.common;

import eu.mshade.enderchest.entity.DefaultTameable;
import eu.mshade.enderframe.entity.Tameable;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagMarshal;

public class DefaultTameableMarshal implements BinaryTagMarshal<Tameable> {

    @Override
    public BinaryTag<?> serialize(BinaryTagDriver binaryTagDriver, Tameable tameable) {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();

        compoundBinaryTag.putBoolean("isTame", tameable.isTamed());
        compoundBinaryTag.putBoolean("isSitting", tameable.isSitting());
        compoundBinaryTag.putString("owner", tameable.getOwner());

        return compoundBinaryTag;
    }

    @Override
    public Tameable deserialize(BinaryTagDriver binaryTagDriver, BinaryTag<?> binaryTag)  {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;
        return new DefaultTameable(compoundBinaryTag.getBoolean("isTame"),
                compoundBinaryTag.getBoolean("isSitting"),
                compoundBinaryTag.getString("owner"));
    }
}
