package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Wolf;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultWolfEntity extends Wolf {

    public DefaultWolfEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, boolean isAngry, boolean begging, boolean collarColor, boolean isSitting, boolean isTame, String owner) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, isAngry, begging, collarColor, isSitting, isTame, owner);
    }

    public DefaultWolfEntity(Location location, int entityId, float health, boolean isAngry, boolean begging, boolean collarColor, boolean isSitting, boolean isTame, String owner) {
        super(location, entityId, health, isAngry, begging, collarColor, isSitting, isTame, owner);
    }

    public DefaultWolfEntity(Location location, int entityId) {
        super(location, entityId);
    }
}
