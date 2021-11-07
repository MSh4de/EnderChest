package eu.mshade.enderchest.entity.marshal.entity;

import eu.mshade.enderchest.entity.DefaultArrowEntity;
import eu.mshade.enderframe.entity.Arrow;
import eu.mshade.enderframe.entity.Entity;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;

import java.lang.reflect.Type;

public class DefaultArrowMarshal extends DefaultEntityMarshal {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, Entity entity, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) super.serialize(binaryTagMarshal, type, entity, parameterContainer);

        Arrow arrow = (Arrow)entity;
        compoundBinaryTag.putBoolean("isCritical", arrow.isCritical());

        return compoundBinaryTag;
    }

    @Override
    public Entity deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        Entity entity = super.deserialize(binaryTagMarshal, type, binaryTag, parameterContainer);
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;

        return new DefaultArrowEntity(entity.getLocation(),
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
                compoundBinaryTag.getBoolean("isCritical"));
    }
}
