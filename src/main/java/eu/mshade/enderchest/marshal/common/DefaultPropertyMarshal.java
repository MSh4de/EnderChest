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
        if (property.getSignature() != null) compoundBinaryTag.putString("signature", property.getSignature());

        return compoundBinaryTag;
    }

    @Override
    public Property deserialize(BinaryTagDriver binaryTagDriver, BinaryTag<?> binaryTag) {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;
        String signature = compoundBinaryTag.containsKey("signature") ? compoundBinaryTag.getString("signature") : null;

        return new Property(compoundBinaryTag.getString("name"),
                compoundBinaryTag.getString("value"),
                signature);
    }
}
