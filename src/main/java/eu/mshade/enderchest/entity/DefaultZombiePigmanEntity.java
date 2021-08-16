package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.ZombiePigman;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultZombiePigmanEntity extends ZombiePigman {
    
    public DefaultZombiePigmanEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, boolean isVillager, boolean isConverting, int age, boolean isAgeLocked) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, isVillager, isConverting, age, isAgeLocked);
    }

    public DefaultZombiePigmanEntity(Location location, int entityId, float health, boolean isVillager, boolean isConverting, boolean isAgeLocked, int age) {
        super(location, entityId, health, isVillager, isConverting, isAgeLocked, age);
    }

    public DefaultZombiePigmanEntity(Location location, int entityId) {
        super(location, entityId);
    }
}
