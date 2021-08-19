package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Enderman;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultEndermanEntity extends Enderman {

    public DefaultEndermanEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, short carriedBlock, byte carriedBlockData, boolean isScreaming) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, carriedBlock, carriedBlockData, isScreaming);
    }

    public DefaultEndermanEntity(Location location, int entityId, float health, short carriedBlock, byte carriedBlockData, boolean isScreaming) {
        super(location, entityId, health, carriedBlock, carriedBlockData, isScreaming);
    }

    public DefaultEndermanEntity(Location location, int entityId) {
        super(location, entityId);
    }
}
