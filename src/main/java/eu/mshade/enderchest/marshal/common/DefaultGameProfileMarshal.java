package eu.mshade.enderchest.marshal.common;

import eu.mshade.enderframe.mojang.GameProfile;
import eu.mshade.enderframe.mojang.Property;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.BinaryTagType;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.entity.ListBinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagMarshal;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DefaultGameProfileMarshal implements BinaryTagMarshal<GameProfile> {

    @Override
    public BinaryTag<?> serialize(BinaryTagDriver binaryTagDriver, GameProfile gameProfile) throws Exception {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();

        compoundBinaryTag.putBinaryTag("uuid", binaryTagDriver.marshal(gameProfile.getId(), UUID.class));
        compoundBinaryTag.putString("name", gameProfile.getName());

        ListBinaryTag properties = new ListBinaryTag(BinaryTagType.COMPOUND);

        gameProfile.getProperties().forEach((s, property) -> properties.add(binaryTagDriver.marshal(property)));
        compoundBinaryTag.putBinaryTag("properties", properties);
        return compoundBinaryTag;
    }

    @Override
    public GameProfile deserialize(BinaryTagDriver binaryTagDriver, BinaryTag<?> binaryTag) {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;

        List<Property> properties = new ArrayList<>();

        ((ListBinaryTag) compoundBinaryTag.getBinaryTag("properties")).forEach(property -> properties.add(binaryTagDriver.unMarshal(property, Property.class)));

        return new GameProfile(
                binaryTagDriver.unMarshal(compoundBinaryTag.getBinaryTag("uuid"), UUID.class),
                compoundBinaryTag.getString("name"),
                properties
                );
    }
}
