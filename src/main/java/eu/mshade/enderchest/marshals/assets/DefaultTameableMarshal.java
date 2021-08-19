package eu.mshade.enderchest.marshals.assets;

import eu.mshade.enderchest.entity.DefaultTameableEntity;
import eu.mshade.enderframe.entity.Tameable;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshalBuffer;

import java.lang.reflect.Type;

public class DefaultTameableMarshal implements BinaryTagMarshalBuffer<Tameable> {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, Tameable tameable, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();

        compoundBinaryTag.putBoolean("isTamed", tameable.isTamed());
        compoundBinaryTag.putBoolean("isSitting", tameable.isSitting());
        compoundBinaryTag.putString("owner", tameable.getOwner());

        return compoundBinaryTag;
    }

    @Override
    public Tameable deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;
        return new DefaultTameableEntity(compoundBinaryTag.getBoolean("isTamed"),
                compoundBinaryTag.getBoolean("isSitting"),
                compoundBinaryTag.getString("owner"));
    }
}
