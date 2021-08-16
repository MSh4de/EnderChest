package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Creeper;
import eu.mshade.enderframe.entity.CreeperState;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultCreeperEntity extends Creeper {

    public DefaultCreeperEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, CreeperState creeperState, boolean isPowered) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, creeperState, isPowered);
    }

    public DefaultCreeperEntity(Location location, int entityId, float health, CreeperState creeperState, boolean isPowered) {
        super(location, entityId, health, creeperState, isPowered);
    }

    public DefaultCreeperEntity(Location location, int entityId) {
        super(location, entityId);
    }
}
