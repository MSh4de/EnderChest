package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.*;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultHorseEntity extends Horse {

    public DefaultHorseEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, boolean hasSaddle, boolean hasChest, boolean isBred, boolean isRearing, boolean mouthOpen, HorseType horseType, HorseColor horseColor, HorseStyle horseStyle, HorseArmor horseArmor, boolean isSitting, boolean isTame, String owner, int age) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, hasSaddle, hasChest, isBred, isRearing, mouthOpen, horseType, horseColor, horseStyle, horseArmor, isSitting, isTame, owner, age);
    }

    public DefaultHorseEntity(Location location, int entityId, float health, boolean hasSaddle, boolean hasChest, boolean isBred, boolean isRearing, boolean mouthOpen, HorseType horseType, HorseColor horseColor, HorseStyle horseStyle, HorseArmor horseArmor, boolean isSitting, boolean isTame, String owner, boolean isAgeLocked, int age, Vector vehicleVelocity) {
        super(location, entityId, health, hasSaddle, hasChest, isBred, isRearing, mouthOpen, horseType, horseColor, horseStyle, horseArmor, isSitting, isTame, owner, isAgeLocked, age, vehicleVelocity);
    }

    public DefaultHorseEntity(Location location, int entityId) {
        super(location, entityId);
    }

    @Override
    public void tick() {

    }
}
