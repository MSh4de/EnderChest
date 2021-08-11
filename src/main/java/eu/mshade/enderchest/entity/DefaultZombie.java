package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.EntityType;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.entity.Zombie;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultZombie extends Zombie {

    public DefaultZombie(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, boolean isChild, boolean isVillager, boolean isConverting, int age) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, isChild, isVillager, isConverting, age);
    }

    public DefaultZombie(Location location, int entityId, float health, boolean isChild, boolean isVillager, boolean isConverting) {
        super(location, entityId, health, isChild, isVillager, isConverting, isChild ? 0 : 1);
    }

    public DefaultZombie(Location location, int entityId) {
        super(location, entityId, 20f, false, false, false, 1);
    }
}
