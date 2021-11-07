package eu.mshade.enderchest.entity.marshal.entity;

import eu.mshade.enderchest.entity.DefaultEnderCrystalEntity;
import eu.mshade.enderframe.entity.EnderCrystal;
import eu.mshade.enderframe.entity.Entity;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;

import java.lang.reflect.Type;

public class DefaultEnderCrystalMarshal extends DefaultEntityMarshal {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, Entity entity, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) super.serialize(binaryTagMarshal, type, entity, parameterContainer);
        EnderCrystal enderCrystal = (EnderCrystal)entity;

        compoundBinaryTag.putInt("health", enderCrystal.getHealth());
        return compoundBinaryTag;
    }

    @Override
    public Entity deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        Entity entity = super.deserialize(binaryTagMarshal, type, binaryTag, parameterContainer);
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;

        return new DefaultEnderCrystalEntity(entity.getLocation(),
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
                entity.getUniqueId(),
                compoundBinaryTag.getInt("health"));
    }
}
