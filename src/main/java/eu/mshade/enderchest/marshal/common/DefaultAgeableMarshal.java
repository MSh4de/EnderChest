package eu.mshade.enderchest.marshal.common;

import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagMarshal;

public class DefaultAgeableMarshal implements BinaryTagMarshal<Ageable> {

    @Override
    public BinaryTag<?> serialize(BinaryTagDriver binaryTagDriver, Ageable ageable)  {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();

        compoundBinaryTag.putInt("age", ageable.getAge());
        compoundBinaryTag.putBoolean("ageLock", ageable.getAgeLock());

        return compoundBinaryTag;
    }

    @Override
    public Ageable deserialize(BinaryTagDriver binaryTagDriver, BinaryTag<?> binaryTag) {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;

        return new DefaultAgeable(compoundBinaryTag.getInt("age"),
                compoundBinaryTag.getBoolean("ageLock"));
    }
}
