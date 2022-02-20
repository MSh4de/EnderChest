package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Arrow;
import eu.mshade.enderframe.entity.ProjectileSource;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultArrow extends Arrow {

    public DefaultArrow(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, boolean isCritical) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, isCritical);
    }

    public DefaultArrow(Location location, int entityId, boolean isCritical, ProjectileSource source, boolean isBouncy) {
        super(location, entityId, isCritical, source, isBouncy);
    }

    public DefaultArrow(Location location, int entityId) {
        super(location, entityId);
    }

    @Override
    public void tick() {

    }
}
