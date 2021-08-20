package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.GameMode;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.mojang.GameProfile;
import eu.mshade.enderframe.mojang.SkinParts;
import eu.mshade.enderframe.protocol.ProtocolVersion;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;
import eu.mshade.mwork.MOptional;

import java.net.SocketAddress;
import java.util.UUID;

public class DefaultPlayerEntity extends Player {

    public DefaultPlayerEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, EnderFrameSessionHandler enderFrameSessionHandler, SocketAddress socketAddress, ProtocolVersion protocolVersion, SkinParts skinParts, boolean unused, float absorptionHearts, int score, MOptional<String> displayName, GameMode gameMode, GameProfile gameProfile) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, enderFrameSessionHandler, socketAddress, protocolVersion, skinParts, unused, absorptionHearts, score, displayName, gameMode, gameProfile);
    }

    public DefaultPlayerEntity(Location location, int entityId, EnderFrameSessionHandler enderFrameSessionHandler, SocketAddress socketAddress, ProtocolVersion protocolVersion, GameMode gameMode, GameProfile gameProfile) {
        super(location, entityId, enderFrameSessionHandler, socketAddress, protocolVersion, gameMode, gameProfile);
    }

    @Override
    public void tick() {

    }
}
