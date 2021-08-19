package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Arrow;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultArrowEntity extends Arrow {

    public DefaultArrowEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, boolean isCritical) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, isCritical);
    }

    public DefaultArrowEntity(Location location, int entityId, boolean isCritical) {
        super(location, new Vector(), entityId, isCritical);
    }

    public DefaultArrowEntity(Location location, int entityId) {
        this(location, entityId, false);
    }
}
