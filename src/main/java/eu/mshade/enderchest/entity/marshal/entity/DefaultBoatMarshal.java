package eu.mshade.enderchest.entity.marshal.entity;

import eu.mshade.enderchest.entity.DefaultBoatEntity;
import eu.mshade.enderframe.entity.Boat;
import eu.mshade.enderframe.entity.Damageable;
import eu.mshade.enderframe.entity.Entity;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;

import java.lang.reflect.Type;

public class DefaultBoatMarshal extends DefaultEntityMarshal {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, Entity entity, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) super.serialize(binaryTagMarshal, type, entity, parameterContainer);
        Boat boat = (Boat) entity;

        compoundBinaryTag.putInt("lastHit", boat.getLastHit());
        compoundBinaryTag.putInt("forwardDirection", boat.getForwardDirection());
        compoundBinaryTag.addCompound((CompoundBinaryTag) binaryTagMarshal.marshal(entity, Damageable.class));

        return compoundBinaryTag;
    }

    @Override
    public Entity deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        Entity entity = super.deserialize(binaryTagMarshal, type, binaryTag, parameterContainer);
        Damageable damageable = binaryTagMarshal.unMarshal(binaryTag, Damageable.class);
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;

        return new DefaultBoatEntity(entity.getLocation(),
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
                compoundBinaryTag.getInt("lastHit"),
                compoundBinaryTag.getInt("forwardDirection"),
                damageable.getDamageTaken());
    }
}
