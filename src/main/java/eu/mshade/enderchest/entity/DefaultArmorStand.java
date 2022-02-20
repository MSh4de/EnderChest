package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.ArmorStand;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Rotation;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultArmorStandEntity extends ArmorStand {

    public DefaultArmorStandEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, boolean isSmall, boolean hasGravity, boolean hasArms, boolean removedBasePlate, boolean marker, Rotation headPosition, Rotation bodyPosition, Rotation leftArmPosition, Rotation rightArmPosition, Rotation leftLegPosition, Rotation rightLegPosition) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, isSmall, hasGravity, hasArms, removedBasePlate, marker, headPosition, bodyPosition, leftArmPosition, rightArmPosition, leftLegPosition, rightLegPosition);
    }

    public DefaultArmorStandEntity(Location location, int entityId, boolean isSmall, boolean hasGravity, boolean removedBasePlate, boolean marker) {
        super(location, entityId, isSmall, hasGravity, removedBasePlate, marker);
    }

    public DefaultArmorStandEntity(Location location, int entityId) {
        this(location, entityId, false, true, false, false);
    }

    @Override
    public void tick() {

    }
}
