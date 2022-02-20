package eu.mshade.enderchest.entity.marshal.entity;

import eu.mshade.enderchest.entity.DefaultArmorStand;
import eu.mshade.enderframe.entity.ArmorStand;
import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.LivingEntity;
import eu.mshade.enderframe.world.Rotation;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;

import java.lang.reflect.Type;

public class DefaultArmorStandMarshal extends DefaultLivingEntityMarshal {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, Entity entity, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) super.serialize(binaryTagMarshal, type, entity, parameterContainer);

        ArmorStand armorStand = (ArmorStand)entity;

        compoundBinaryTag.putBoolean("isSmall", armorStand.isSmall());
        compoundBinaryTag.putBoolean("hasGravity", armorStand.hasGravity());
        compoundBinaryTag.putBoolean("hasArms", armorStand.hasArms());
        compoundBinaryTag.putBoolean("isBasePlateRemoved", armorStand.isRemovedBasePlate());
        compoundBinaryTag.putBoolean("isMarker", armorStand.isMarker());
        compoundBinaryTag.putBinaryTag("headPosition", binaryTagMarshal.marshal(armorStand.getHeadPosition()));
        compoundBinaryTag.putBinaryTag("bodyPosition", binaryTagMarshal.marshal(armorStand.getBodyPosition()));
        compoundBinaryTag.putBinaryTag("leftArmPosition", binaryTagMarshal.marshal(armorStand.getLeftArmPosition()));
        compoundBinaryTag.putBinaryTag("rightArmPosition", binaryTagMarshal.marshal(armorStand.getRightArmPosition()));
        compoundBinaryTag.putBinaryTag("leftLegPosition", binaryTagMarshal.marshal(armorStand.getLeftLegPosition()));
        compoundBinaryTag.putBinaryTag("rightLegPosition", binaryTagMarshal.marshal(armorStand.getRightLegPosition()));

        return compoundBinaryTag;
    }

    @Override
    public LivingEntity deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        LivingEntity livingEntity = super.deserialize(binaryTagMarshal, type, binaryTag, parameterContainer);
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;

        return new DefaultArmorStand(livingEntity.getLocation(),
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
                livingEntity.hasAI(),
                compoundBinaryTag.getBoolean("isSmall"),
                compoundBinaryTag.getBoolean("hasGravity"),
                compoundBinaryTag.getBoolean("hasArms"),
                compoundBinaryTag.getBoolean("isBasePlateRemoved"),
                compoundBinaryTag.getBoolean("isMarker"),
                binaryTagMarshal.unMarshal(compoundBinaryTag.getBinaryTag("headPosition"), Rotation.class),
                binaryTagMarshal.unMarshal(compoundBinaryTag.getBinaryTag("bodyPosition"), Rotation.class),
                binaryTagMarshal.unMarshal(compoundBinaryTag.getBinaryTag("leftArmPosition"), Rotation.class),
                binaryTagMarshal.unMarshal(compoundBinaryTag.getBinaryTag("rightArmPosition"), Rotation.class),
                binaryTagMarshal.unMarshal(compoundBinaryTag.getBinaryTag("leftLegPosition"), Rotation.class),
                binaryTagMarshal.unMarshal(compoundBinaryTag.getBinaryTag("rightLegPosition"), Rotation.class));
    }
}
