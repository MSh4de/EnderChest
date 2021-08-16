package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Rabbit;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultRabbitEntity extends Rabbit {

    public DefaultRabbitEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, byte rabbitType) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, rabbitType);
    }

    public DefaultRabbitEntity(Location location, int entityId, float health, byte rabbitType, int age) {
        super(location, entityId, health, rabbitType, age);
    }

    public DefaultRabbitEntity(Location location, int entityId) {
        super(location, entityId);
    }
}
