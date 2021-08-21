package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Guardian;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultGuardianEntity extends Guardian {

    public DefaultGuardianEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, boolean isElderly, boolean isRetractingSpikes, int targetEntityId) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, isElderly, isRetractingSpikes, targetEntityId);
    }

    public DefaultGuardianEntity(Location location, int entityId, float health, boolean isElderly, boolean isRetractingSpikes) {
        super(location, entityId, health, isElderly, isRetractingSpikes);
    }

    public DefaultGuardianEntity(Location location, int entityId) {
        super(location, entityId);
    }

    @Override
    public void tick() {

    }
}
