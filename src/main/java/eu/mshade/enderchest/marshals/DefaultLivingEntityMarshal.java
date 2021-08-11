package eu.mshade.enderchest.marshals;

import eu.mshade.enderchest.entity.DefaultLivingEntity;
import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.LivingEntity;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;

import java.lang.reflect.Type;

public class DefaultLivingEntityMarshal extends DefaultEntityMarshal {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, Entity entity, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) super.serialize(binaryTagMarshal, type, entity, parameterContainer);
        LivingEntity livingEntity = (LivingEntity) entity;

        compoundBinaryTag.putFloat("health",livingEntity.getHealth());
        compoundBinaryTag.putInt("potionEffectColor", livingEntity.getPotionEffectColor());
        compoundBinaryTag.putBoolean("isPotionEffectAmbient", livingEntity.isPotionEffectAmbient());
        compoundBinaryTag.putByte("numberOfArrowInEntity", livingEntity.getNumberOfArrowInEntity());
        compoundBinaryTag.putBoolean("isAIDisable", livingEntity.isAIDisable());
        return compoundBinaryTag;
    }

    @Override
    public LivingEntity deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        Entity entity = super.deserialize(binaryTagMarshal, type, binaryTag, parameterContainer);
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;
        return new DefaultLivingEntity(entity.getLocation(),
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
                compoundBinaryTag.getFloat("health"),
                compoundBinaryTag.getInt("potionEffectColor"),
                compoundBinaryTag.getBoolean("isPotionEffectAmbient"),
                compoundBinaryTag.getByte("numberOfArrowInEntity"),
                compoundBinaryTag.getBoolean("isAIDisable"));
    }
}
