package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Spider;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultSpiderEntity extends Spider {

    public DefaultSpiderEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, boolean isClimbing) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, isClimbing);
    }

    public DefaultSpiderEntity(Location location, int entityId, float health, boolean isClimbing) {
        super(location, entityId, health, isClimbing);
    }

    public DefaultSpiderEntity(Location location, int entityId) {
        super(location, entityId);
    }
}