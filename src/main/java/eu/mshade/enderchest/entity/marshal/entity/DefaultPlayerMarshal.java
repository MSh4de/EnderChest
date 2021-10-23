package eu.mshade.enderchest.entity.marshal.entity;

import eu.mshade.enderchest.entity.DefaultPlayerEntity;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.GameMode;
import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.LivingEntity;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.mojang.GameProfile;
import eu.mshade.mwork.MOptional;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;

import java.lang.reflect.Type;

public class DefaultPlayerMarshal extends DefaultLivingEntityMarshal {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, Entity entity, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) super.serialize(binaryTagMarshal, type, entity, parameterContainer);
        Player player = (Player)entity;

        compoundBinaryTag.putFloat("absorptionHearts", player.getAbsorptionHearts());
        compoundBinaryTag.putInt("score", player.getScore());
        compoundBinaryTag.putBinaryTag("gameMode", binaryTagMarshal.marshal(player.getGameMode()));
        compoundBinaryTag.putBinaryTag("gameProfile", binaryTagMarshal.marshal(player.getGameProfile()));

        return compoundBinaryTag;
    }

    @Override
    public Player deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        LivingEntity livingEntity = super.deserialize(binaryTagMarshal, type, binaryTag, parameterContainer);
        EnderFrameSessionHandler enderFrameSessionHandler = parameterContainer.getContainer(EnderFrameSessionHandler.class);
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;

        return new DefaultPlayerEntity(livingEntity.getLocation(),
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
                enderFrameSessionHandler,
                false,
                compoundBinaryTag.getFloat("absorptionHeart"),
                compoundBinaryTag.getInt("score"),
                MOptional.empty(),
                binaryTagMarshal.unMarshal(compoundBinaryTag.getBinaryTag("gameMode"), GameMode.class),
                binaryTagMarshal.unMarshal(compoundBinaryTag.getBinaryTag("gameProfile"), GameProfile.class));
    }
}
