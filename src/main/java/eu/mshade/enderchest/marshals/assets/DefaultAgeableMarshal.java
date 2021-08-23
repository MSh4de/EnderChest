package eu.mshade.enderchest.marshals.assets;

import eu.mshade.enderchest.entity.DefaultAgeableEntity;
import eu.mshade.enderframe.entity.Ageable;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshalBuffer;

import java.lang.reflect.Type;

public class DefaultAgeableMarshal implements BinaryTagMarshalBuffer<Ageable> {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, Ageable ageable, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();

        compoundBinaryTag.putInt("age", ageable.getAge());
        compoundBinaryTag.putBoolean("ageLock", ageable.getAgeLock());

        return compoundBinaryTag;
    }

    @Override
    public Ageable deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;

        return new DefaultAgeableEntity(compoundBinaryTag.getInt("age"),
                compoundBinaryTag.getBoolean("ageLock"));
    }
}
