package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.EntityType;
import eu.mshade.enderframe.entity.Slime;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultSlimeEntity extends Slime {

    public DefaultSlimeEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, EntityType entityType, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, byte size) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, entityType, health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, size);
    }

    public DefaultSlimeEntity(Location location, EntityType entityType, int entityId, float health, byte size) {
        super(location, entityType, entityId, health, size);
    }

    public DefaultSlimeEntity(Location location, int entityId) {
        super(location, entityId);
    }
}
