package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Skeleton;
import eu.mshade.enderframe.entity.SkeletonType;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.UUID;

public class DefaultSkeleton extends Skeleton {

    public DefaultSkeleton(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowInEntity, boolean isAIDisable, SkeletonType skeletonType) {
        super(location, velocity, entityId, isFire, isSneaking, isSprinting, isEating, isInvisible, airTicks, customName, isCustomNameVisible, isSilent, uuid, health, potionEffectColor, isPotionEffectAmbient, numberOfArrowInEntity, isAIDisable, skeletonType);
    }

    public DefaultSkeleton(Location location, int entityId, float health, SkeletonType skeletonType) {
        super(location, entityId, health, skeletonType);
    }

    public DefaultSkeleton(Location location, int entityId) {
        super(location, entityId);
    }

    @Override
    public void tick() {

    }
}
