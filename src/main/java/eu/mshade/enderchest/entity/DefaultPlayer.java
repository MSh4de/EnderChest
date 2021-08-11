package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.entity.EntityType;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.mojang.SkinParts;
import eu.mshade.enderframe.protocol.ProtocolVersion;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultPlayer extends Player {

    public DefaultPlayer(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, EnderFrameSessionHandler enderFrameSessionHandler, String name, SocketAddress socketAddress, ProtocolVersion protocolVersion, int ping, SkinParts skinParts, boolean unused, float absorptionHearts, int score) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, new ConcurrentLinkedQueue<>(), health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, enderFrameSessionHandler, name, socketAddress, protocolVersion, ping, skinParts, unused, absorptionHearts, score);
    }

    public DefaultPlayer(Location location, int entityId, EnderFrameSessionHandler enderFrameSessionHandler, String name, SocketAddress socketAddress, ProtocolVersion protocolVersion) {
        super(location, entityId, enderFrameSessionHandler, name, socketAddress, protocolVersion);
    }
}
