package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Item;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultItemEntity extends Item {

    public DefaultItemEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid);
    }

    public DefaultItemEntity(Location location, int entityId) {
        super(location, entityId);
    }
}
