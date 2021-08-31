package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Zombie;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultZombieEntity extends Zombie {

    public DefaultZombieEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, boolean isVillager, boolean isConverting, int age, boolean isAgeLocked) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, isVillager, isConverting, age, isAgeLocked);
    }

    public DefaultZombieEntity(Location location, int entityId, float health, boolean isChild, boolean isVillager, boolean isConverting, boolean isAgeLocked) {
        super(location, entityId, health, isVillager, isConverting, isAgeLocked, isChild ? -24000 : 0);
    }

    public DefaultZombieEntity(Location location, int entityId) {
        this(location, entityId, 20f, false, false, false, false);
    }

    @Override
    public void tick() {
        move(location.add(1, 0, 0));
    }
}
