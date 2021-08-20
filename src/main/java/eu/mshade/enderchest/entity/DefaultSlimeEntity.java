package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.EntityType;
import eu.mshade.enderframe.entity.Slime;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultSlimeEntity extends Slime {

    public DefaultSlimeEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, byte size) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, EntityType.SLIME, health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, size);
    }

    public DefaultSlimeEntity(Location location, int entityId, float health, byte size) {
        super(location, EntityType.SLIME, entityId, health, size);
    }

    public DefaultSlimeEntity(Location location, int entityId) {
        super(location, entityId);
    }

    @Override
    public void tick() {

    }
}
