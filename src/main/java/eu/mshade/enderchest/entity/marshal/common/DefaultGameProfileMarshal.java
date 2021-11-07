package eu.mshade.enderchest.entity.marshal.common;

import eu.mshade.enderframe.mojang.GameProfile;
import eu.mshade.enderframe.mojang.Property;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagType;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.entity.ListBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshalBuffer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DefaultGameProfileMarshal implements BinaryTagMarshalBuffer<GameProfile> {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, GameProfile gameProfile, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();

        compoundBinaryTag.putBinaryTag("uuid", binaryTagMarshal.marshal(gameProfile.getId(), UUID.class));
        compoundBinaryTag.putString("name", gameProfile.getName());

        ListBinaryTag properties = new ListBinaryTag(BinaryTagType.COMPOUND);

        gameProfile.getProperties().forEach((s, property) -> properties.add(binaryTagMarshal.marshal(property)));
        compoundBinaryTag.putBinaryTag("properties", properties);
        return compoundBinaryTag;
    }

    @Override
    public GameProfile deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;

        List<Property> properties = new ArrayList<>();

        ((ListBinaryTag) compoundBinaryTag.getBinaryTag("properties")).forEach(property -> properties.add(binaryTagMarshal.unMarshal(property, Property.class)));

        return new GameProfile(
                binaryTagMarshal.unMarshal(compoundBinaryTag.getBinaryTag("uuid"), UUID.class),
                compoundBinaryTag.getString("name"),
                properties
                );
    }
}
