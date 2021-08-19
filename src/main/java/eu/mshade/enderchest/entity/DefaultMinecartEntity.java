package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.EntityType;
import eu.mshade.enderframe.entity.Minecart;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultMinecartEntity extends Minecart {

    public DefaultMinecartEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, int shakingPower, int shakingDirection, int blockId, int blockData, int blockYPosition, boolean showBlock, float damageTaken) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, EntityType.MINECART, shakingPower, shakingDirection, blockId, blockData, blockYPosition, showBlock, damageTaken);
    }

    public DefaultMinecartEntity(Location location, int entityId, int shakingPower, int shakingDirection, int blockId, int blockData, int blockYPosition, boolean showBlock, float damageTaken) {
        super(location, entityId, EntityType.MINECART, shakingPower, shakingDirection, blockId, blockData, blockYPosition, showBlock, damageTaken);
    }

    public DefaultMinecartEntity(Location location, int entityId) {
        this(location, entityId, 0, 0, 0, 0, 0, false, 0f);
    }
}
