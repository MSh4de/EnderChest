package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.FurnaceMinecart;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultFurnaceMinecartEntity extends FurnaceMinecart {


    public DefaultFurnaceMinecartEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, int shakingPower, int shakingDirection, int blockId, int blockData, int blockYPosition, boolean showBlock, float damageTaken, boolean isPowered) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, shakingPower, shakingDirection, blockId, blockData, blockYPosition, showBlock, damageTaken, isPowered);
    }

    public DefaultFurnaceMinecartEntity(Location location, int entityId, boolean isPowered) {
        super(location, entityId, isPowered);
    }

    public DefaultFurnaceMinecartEntity(Location location, int entityId) {
        super(location, entityId);
    }

    @Override
    public void tick() {

    }
}
