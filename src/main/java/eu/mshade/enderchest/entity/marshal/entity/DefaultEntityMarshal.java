package eu.mshade.enderchest.entity.marshal.entity;

import eu.mshade.enderchest.entity.DefaultEntity;
import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.EntityIdManager;
import eu.mshade.enderframe.entity.EntityType;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshalBuffer;

import java.lang.reflect.Type;
import java.util.UUID;

public class DefaultEntityMarshal implements BinaryTagMarshalBuffer<Entity> {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, Entity entity, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();

        compoundBinaryTag.putBinaryTag("location", binaryTagMarshal.marshal(entity.getLocation()));
        compoundBinaryTag.putBinaryTag("velocity", binaryTagMarshal.marshal(entity.getVelocity()));
        compoundBinaryTag.putBoolean("isFire", entity.isFire());
        compoundBinaryTag.putBoolean("isSneaking", entity.isSneaking());
        compoundBinaryTag.putBoolean("isSprinting", entity.isSprinting());
        compoundBinaryTag.putBoolean("isEating", entity.isEating());
        compoundBinaryTag.putBoolean("isInvisible", entity.isInvisible());
        compoundBinaryTag.putShort("airTicks", entity.getAirTicks());
        compoundBinaryTag.putString("customName", entity.getCustomName());
        compoundBinaryTag.putBoolean("isCustomNameVisible", entity.isCustomNameVisible());
        compoundBinaryTag.putBoolean("isSilent", entity.isSilent());
        compoundBinaryTag.putBinaryTag("uuid", binaryTagMarshal.marshal(entity.getUniqueId()));
        compoundBinaryTag.putString("entityType", entity.getEntityType().name());

        return compoundBinaryTag;
    }

    @Override
    public Entity deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;
        return new DefaultEntity(binaryTagMarshal.unMarshal(compoundBinaryTag.getBinaryTag("location"), Location.class, parameterContainer),
                binaryTagMarshal.unMarshal(compoundBinaryTag.getBinaryTag("velocity"), Vector.class),
                EntityIdManager.get().getFreeId(),
                compoundBinaryTag.getBoolean("isFire"),
                compoundBinaryTag.getBoolean("isSneaking"),
                compoundBinaryTag.getBoolean("isSprinting"),
                compoundBinaryTag.getBoolean("isEating"),
                compoundBinaryTag.getBoolean("isInvisible"),
                compoundBinaryTag.getShort("airTicks"),
                compoundBinaryTag.getString("customName"),
                compoundBinaryTag.getBoolean("isCustomNameVisible"),
                compoundBinaryTag.getBoolean("isSilent"),
                binaryTagMarshal.unMarshal(compoundBinaryTag.getBinaryTag("uuid"), UUID.class),
                EntityType.getEntityTypeByName(compoundBinaryTag.getString("entityType")));
    }
}
