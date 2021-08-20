package eu.mshade.enderchest.marshals.entity;

import eu.mshade.enderchest.entity.DefaultMinecartEntity;
import eu.mshade.enderframe.entity.Damageable;
import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.EntityType;
import eu.mshade.enderframe.entity.Minecart;
import eu.mshade.enderframe.world.Vector;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;

import java.lang.reflect.Type;

public class DefaultMinecartMarshal extends DefaultEntityMarshal {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, Entity entity, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) super.serialize(binaryTagMarshal, type, entity, parameterContainer);
        Minecart minecart = (Minecart) entity;

        compoundBinaryTag.putString("entityType", entity.getType().name());
        compoundBinaryTag.putInt("shakingPower", minecart.getShakingPower());
        compoundBinaryTag.putInt("shakingDirection", minecart.getShakingDirection());
        compoundBinaryTag.putInt("blockId", minecart.getBlockId());
        compoundBinaryTag.putInt("blockYPosition", minecart.getBlockYPosition());
        compoundBinaryTag.putBoolean("isShowingBlock", minecart.isShowBlock());
        compoundBinaryTag.addCompound((CompoundBinaryTag) binaryTagMarshal.marshal(minecart, Damageable.class));
        compoundBinaryTag.putBinaryTag("vehicleVelocity", binaryTagMarshal.marshal(((Minecart) entity).getVehicleVelocity()));

        return compoundBinaryTag;
    }

    @Override
    public Entity deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        Entity entity = super.deserialize(binaryTagMarshal, type, binaryTag, parameterContainer);
        Damageable damageable = binaryTagMarshal.unMarshal(binaryTag, Damageable.class);
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;

        return new DefaultMinecartEntity(entity.getLocation(),
                entity.getVelocity(),
                entity.getEntityId(),
                entity.isFire(),
                entity.isSneaking(),
                entity.isSprinting(),
                entity.isEating(),
                entity.isInvisible(),
                entity.getAirTicks(),
                entity.getCustomName(),
                entity.isCustomNameVisible(),
                entity.isSilent(),
                entity.getUUID(),
                EntityType.getEntityTypeByName(compoundBinaryTag.getString("entityType")),
                compoundBinaryTag.getInt("shakingPower"),
                compoundBinaryTag.getInt("shakingDirection"),
                compoundBinaryTag.getInt("blockId"),
                compoundBinaryTag.getInt("blockData"),
                compoundBinaryTag.getInt("blockYPosition"),
                compoundBinaryTag.getBoolean("isShowingBlock"),
                damageable.getDamageTaken(),
                binaryTagMarshal.unMarshal(compoundBinaryTag.getBinaryTag("vehicleVelocity"), Vector.class));
    }
}
