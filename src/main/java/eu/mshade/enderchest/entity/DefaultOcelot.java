package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Ocelot;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultOcelot extends Ocelot {

    public DefaultOcelot(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, byte ocelotType, boolean isSitting, boolean isTame, String owner) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, ocelotType, isSitting, isTame, owner);
    }

    public DefaultOcelot(Location location, int entityId, float health, byte ocelotType, boolean isSitting, boolean isTame, String owner) {
        super(location, entityId, health, ocelotType, isSitting, isTame, owner);
    }

    public DefaultOcelot(Location location, int entityId) {
        super(location, entityId);
    }

    @Override
    public void tick() {

    }
}
