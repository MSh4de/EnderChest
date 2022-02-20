package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Wither;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultWitherEntity extends Wither {

    public DefaultWitherEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, int watchedTarget, int invulnerableTime) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, watchedTarget, invulnerableTime);
    }

    public DefaultWitherEntity(Location location, int entityId, float health, int watchedTarget, int invulnerableTime) {
        super(location, entityId, health, watchedTarget, invulnerableTime);
    }

    public DefaultWitherEntity(Location location, int entityId) {
        super(location, entityId);
    }

    @Override
    public void tick() {

    }
}
