package eu.mshade.enderchest.entity.marshal.entity;

import eu.mshade.enderchest.entity.DefaultWolfEntity;
import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.LivingEntity;
import eu.mshade.enderframe.entity.Tameable;
import eu.mshade.enderframe.entity.Wolf;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;

import java.lang.reflect.Type;

public class DefaultWolfMarshal extends DefaultLivingEntityMarshal {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, Entity entity, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) super.serialize(binaryTagMarshal, type, entity, parameterContainer);

        Wolf wolf = (Wolf)entity;
        compoundBinaryTag.putBoolean("isAngry", wolf.isAngry());
        compoundBinaryTag.putBoolean("isBegging", wolf.isBegging());
        compoundBinaryTag.putBoolean("isCollarColor", wolf.isCollarColor());
        compoundBinaryTag.addCompound((CompoundBinaryTag) binaryTagMarshal.marshal(entity, Tameable.class));

        return compoundBinaryTag;
    }

    @Override
    public LivingEntity deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        LivingEntity livingEntity = super.deserialize(binaryTagMarshal, type, binaryTag, parameterContainer);
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag)binaryTag;
        Tameable tameable = binaryTagMarshal.unMarshal(binaryTag, Tameable.class);

        return new DefaultWolfEntity(livingEntity.getLocation(),
                livingEntity.getVelocity(),
                livingEntity.getEntityId(),
                livingEntity.isFire(),
                livingEntity.isSneaking(),
                livingEntity.isSprinting(),
                livingEntity.isEating(),
                livingEntity.isInvisible(),
                livingEntity.getAirTicks(),
                livingEntity.getCustomName(),
                livingEntity.isCustomNameVisible(),
                livingEntity.isSilent(),
                livingEntity.getUniqueId(),
                livingEntity.getHealth(),
                livingEntity.getPotionEffectColor(),
                livingEntity.isPotionEffectAmbient(),
                livingEntity.getNumberOfArrowInEntity(),
                livingEntity.isAIDisable(),
                compoundBinaryTag.getBoolean("isAngry"),
                compoundBinaryTag.getBoolean("isBegging"),
                compoundBinaryTag.getBoolean("isCollarColor"),
                tameable.isSitting(),
                tameable.isTamed(),
                tameable.getOwner());
    }
}
