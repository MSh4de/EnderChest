package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Boat;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultBoatEntity extends Boat {

    public DefaultBoatEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, int lastHit, int forwardDirection, float damageTaken) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, lastHit, forwardDirection, damageTaken);
    }

    public DefaultBoatEntity(Location location, int entityId, int lastHit, int forwardDirection, float damageTaken) {
        super(location, entityId, lastHit, forwardDirection, damageTaken);
    }

    public DefaultBoatEntity(Location location, int entityId) {
        super(location, entityId);
    }
}
