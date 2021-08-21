package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.EntityType;
import eu.mshade.enderframe.entity.Minecart;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultMinecartEntity extends Minecart {

    public DefaultMinecartEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, EntityType entityType, int shakingPower, int shakingDirection, int blockId, int blockData, int blockYPosition, boolean showBlock, float damageTaken, Vector vehicleVelocity) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, entityType, shakingPower, shakingDirection, blockId, blockData, blockYPosition, showBlock, damageTaken, vehicleVelocity);
    }

    public DefaultMinecartEntity(Location location, int entityId, EntityType entityType, int shakingPower, int shakingDirection, int blockId, int blockData, int blockYPosition, boolean showBlock, float damageTaken, Vector vehicleVelocity) {
        super(location, entityId, entityType, shakingPower, shakingDirection, blockId, blockData, blockYPosition, showBlock, damageTaken, vehicleVelocity);
    }

    public DefaultMinecartEntity(Location location, EntityType entityType, int entityId) {
        super(location, entityType, entityId);
    }

    @Override
    public void tick() {

    }

    public DefaultMinecartEntity(Location location, int entityId) {
        super(location, entityId);
    }
}
