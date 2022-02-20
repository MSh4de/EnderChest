package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Rabbit;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultRabbit extends Rabbit {

    public DefaultRabbit(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, byte rabbitType, int age, boolean isAgeLocked) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, rabbitType, age, isAgeLocked);
    }

    public DefaultRabbit(Location location, int entityId, float health, byte rabbitType, int age) {
        super(location, entityId, health, rabbitType, age);
    }

    public DefaultRabbit(Location location, int entityId) {
        super(location, entityId);
    }

    @Override
    public void tick() {

    }
}
