package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Chicken;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultChicken extends Chicken {

    public DefaultChicken(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, int age, boolean isAgeLocked) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, age, isAgeLocked);
    }

    public DefaultChicken(Location location, int entityId, float health, int age, boolean isAgeLocked) {
        super(location, entityId, health, age, isAgeLocked);
    }

    public DefaultChicken(Location location, int entityId) {
        super(location, entityId);
    }

    @Override
    public void tick() {

    }
}
