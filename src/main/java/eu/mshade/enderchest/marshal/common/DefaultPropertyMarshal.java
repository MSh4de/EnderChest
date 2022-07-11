package eu.mshade.enderchest.marshal.common;

import eu.mshade.enderframe.mojang.Property;
import eu.mshade.mwork.MOptional;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagMarshal;

import java.lang.reflect.Type;

public class DefaultPropertyMarshal implements BinaryTagMarshal<Property> {
    @Override
    public BinaryTag<?> serialize(BinaryTagDriver binaryTagDriver, Property property) {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();

        compoundBinaryTag.putString("name", property.getName());
        compoundBinaryTag.putString("value", property.getValue());
        property.getSignature().ifPresent(s -> compoundBinaryTag.putString("signature", s));

        return compoundBinaryTag;
    }

    @Override
    public Property deserialize(BinaryTagDriver binaryTagDriver, BinaryTag<?> binaryTag) {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;
        MOptional<String> signature = compoundBinaryTag.containsKey("signature") ? MOptional.of(compoundBinaryTag.getString("signature")) : MOptional.empty();

        return new Property(compoundBinaryTag.getString("name"),
                compoundBinaryTag.getString("value"),
                signature);
    }
}
