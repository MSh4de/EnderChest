package eu.mshade.enderchest.marshals.entity;

import eu.mshade.enderchest.entity.DefaultSkeletonEntity;
import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.LivingEntity;
import eu.mshade.enderframe.entity.Skeleton;
import eu.mshade.enderframe.entity.SkeletonType;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;

import java.lang.reflect.Type;

public class DefaultSkeletonMarshal extends DefaultLivingEntityMarshal {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, Entity entity, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) super.serialize(binaryTagMarshal, type, entity, parameterContainer);
        Skeleton skeleton = (Skeleton) entity;

        compoundBinaryTag.putBinaryTag("skeletonType", binaryTagMarshal.marshal(skeleton.getSkeletonType(), SkeletonType.class));
        return compoundBinaryTag;
    }

    @Override
    public LivingEntity deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        LivingEntity livingEntity =  super.deserialize(binaryTagMarshal, type, binaryTag, parameterContainer);
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;

        return new DefaultSkeletonEntity(livingEntity.getLocation(),
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
                binaryTagMarshal.unMarshal(compoundBinaryTag.getBinaryTag("skeletonType"), SkeletonType.class));
    }
}
