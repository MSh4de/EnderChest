package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Blaze;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultBlazeEntity extends Blaze {

    public DefaultBlazeEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, boolean isOnFire) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, new ConcurrentLinkedQueue<>(), health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, isOnFire);
    }

    public DefaultBlazeEntity(Location location, int entityId, float health, boolean onFire) {
        super(location, entityId, health, onFire);
    }

    public DefaultBlazeEntity(Location location, int entityId) {
        this(location, entityId, 20f, false);
    }
}
