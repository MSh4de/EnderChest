package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Sheep;
import eu.mshade.enderframe.entity.SheepColor;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultSheepEntity extends Sheep {

    public DefaultSheepEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, SheepColor sheepColor, boolean isSheared, int age) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, sheepColor, isSheared, age);
    }

    public DefaultSheepEntity(Location location, int entityId, float health, SheepColor sheepColor, boolean isSheared, int age, boolean isAgeLocked) {
        super(location, entityId, health, sheepColor, isSheared, age, isAgeLocked);
    }

    public DefaultSheepEntity(Location location, int entityId) {
        super(location, entityId);
    }

    @Override
    public void tick() {

    }
}
