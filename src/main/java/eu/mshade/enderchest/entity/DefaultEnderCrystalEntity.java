package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.EnderCrystal;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultEnderCrystalEntity extends EnderCrystal {

    public DefaultEnderCrystalEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, int health) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, health);
    }

    public DefaultEnderCrystalEntity(Location location, int entityId, int health) {
        super(location, entityId, health);
    }

    public DefaultEnderCrystalEntity(Location location, int entityId) {
        super(location, entityId);
    }
}
