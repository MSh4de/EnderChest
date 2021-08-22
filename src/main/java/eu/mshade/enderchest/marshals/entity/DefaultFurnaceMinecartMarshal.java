package eu.mshade.enderchest.marshals.entity;

import eu.mshade.enderchest.entity.DefaultFurnaceMinecartEntity;
import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.FurnaceMinecart;
import eu.mshade.enderframe.entity.Minecart;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;

import java.lang.reflect.Type;

public class DefaultFurnaceMinecartMarshal extends DefaultMinecartMarshal {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, Entity entity, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) super.serialize(binaryTagMarshal, type, entity, parameterContainer);
        FurnaceMinecart furnaceMinecart = (FurnaceMinecart) entity;

        compoundBinaryTag.putBoolean("isPowered", furnaceMinecart.isPowered());

        return compoundBinaryTag;
    }

    @Override
    public Entity deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        Minecart entity = (Minecart) super.deserialize(binaryTagMarshal, type, binaryTag, parameterContainer);
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;

        return new DefaultFurnaceMinecartEntity(entity.getLocation(),
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
                compoundBinaryTag.getInt("shakingPower"),
                compoundBinaryTag.getInt("shakingDirection"),
                compoundBinaryTag.getInt("blockId"),
                compoundBinaryTag.getInt("blockData"),
                compoundBinaryTag.getInt("blockYPosition"),
                compoundBinaryTag.getBoolean("isShowingBlock"),
                entity.getDamageTaken(),
                compoundBinaryTag.getBoolean("isPowered"));
    }
}
