package eu.mshade.enderchest.entity.marshal.common;

import eu.mshade.enderchest.entity.DefaultDamageable;
import eu.mshade.enderframe.entity.Damageable;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshalBuffer;

import java.lang.reflect.Type;

public class DefaultDamageableMarshal implements BinaryTagMarshalBuffer<Damageable> {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, Damageable damageable, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();
        compoundBinaryTag.putFloat("damageTaken", damageable.getDamageTaken());

        return compoundBinaryTag;
    }

    @Override
    public Damageable deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;
        return new DefaultDamageable(compoundBinaryTag.getFloat("damageTaken"));
    }
}
