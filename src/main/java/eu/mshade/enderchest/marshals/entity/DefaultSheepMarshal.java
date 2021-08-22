package eu.mshade.enderchest.marshals.entity;

import eu.mshade.enderchest.entity.DefaultSheepEntity;
import eu.mshade.enderframe.entity.*;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;

import java.lang.reflect.Type;

public class DefaultSheepMarshal extends DefaultLivingEntityMarshal {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, Entity entity, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) super.serialize(binaryTagMarshal, type, entity, parameterContainer);

        Sheep sheep = (Sheep)entity;
        compoundBinaryTag.putBinaryTag("sheepColor", binaryTagMarshal.marshal(sheep.getSheepColor(), SheepColor.class));
        compoundBinaryTag.putBoolean("isSheared", sheep.isSheared());
        compoundBinaryTag.addCompound((CompoundBinaryTag) binaryTagMarshal.marshal(entity, Ageable.class));

        return compoundBinaryTag;
    }

    @Override
    public LivingEntity deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        LivingEntity livingEntity = super.deserialize(binaryTagMarshal, type, binaryTag, parameterContainer);
        Ageable ageable = binaryTagMarshal.unMarshal(binaryTag, Ageable.class);
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;

        return new DefaultSheepEntity(livingEntity.getLocation(),
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
                livingEntity.getUUID(),
                livingEntity.getHealth(),
                livingEntity.getPotionEffectColor(),
                livingEntity.isPotionEffectAmbient(),
                livingEntity.getNumberOfArrowInEntity(),
                livingEntity.isAIDisable(),
                binaryTagMarshal.unMarshal(binaryTag, SheepColor.class),
                compoundBinaryTag.getBoolean("isSheared"),
                ageable.getAge(),
                ageable.getAgeLock());
    }
}
