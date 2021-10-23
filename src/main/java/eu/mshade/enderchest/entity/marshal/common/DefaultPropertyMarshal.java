package eu.mshade.enderchest.entity.marshal.common;

import eu.mshade.enderframe.mojang.Property;
import eu.mshade.mwork.MOptional;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshalBuffer;

import java.lang.reflect.Type;

public class DefaultPropertyMarshal implements BinaryTagMarshalBuffer<Property> {
    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, Property property, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();

        compoundBinaryTag.putString("name", property.getName());
        compoundBinaryTag.putString("value", property.getValue());
        property.getSignature().ifPresent(s -> compoundBinaryTag.putString("signature", s));

        return compoundBinaryTag;
    }

    @Override
    public Property deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;
        MOptional<String> signature = compoundBinaryTag.containsKey("signature") ? MOptional.of(compoundBinaryTag.getString("signature")) : MOptional.empty();

        return new Property(compoundBinaryTag.getString("name"),
                compoundBinaryTag.getString("value"),
                signature);
    }
}
