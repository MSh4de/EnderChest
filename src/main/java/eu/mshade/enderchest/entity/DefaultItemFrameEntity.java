package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.ItemFrame;
import eu.mshade.enderframe.item.ItemStack;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultItemFrameEntity extends ItemFrame {

    public DefaultItemFrameEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, byte rotation) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, rotation);
    }

    public DefaultItemFrameEntity(Location location, int entityId, byte rotation, ItemStack content) {
        super(location, entityId, rotation, content);
    }

    public DefaultItemFrameEntity(Location location, int entityId) {
        super(location, entityId);
    }

    @Override
    public void tick() {

    }
}
