package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.EntityType;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultEntity extends Entity {

    private final EntityType entityType;
    private Location location;
    private Vector velocity;
    private final int entityId;
    private boolean isFire;
    private boolean isSneaking;
    private boolean isSprinting;
    private boolean isEating;
    private boolean isInvisible;
    private short airTicks;
    private String customName;
    private boolean isCustomNameVisible;
    private boolean isSilent;
    private final UUID uuid;
    private final Queue<Player> viewers = new ConcurrentLinkedQueue<>();

    public DefaultEntity(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, EntityType entityType) {
        this.location = location;
        this.velocity = velocity;
        this.entityId = entityId;
        this.isFire = isFire;
        this.isSneaking = isSneaking;
        this.isSprinting = isSprinting;
        this.isEating = isEating;
        this.isInvisible = isInvisible;
        this.airTicks = airTicks;
        this.customName = customName;
        this.isCustomNameVisible = isCustomNameVisible;
        this.isSilent = isSilent;
        this.uuid = uuid;
        this.entityType = entityType;
    }


    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public Vector getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public boolean isFire() {
        return isFire;
    }

    @Override
    public void setFire(boolean isFire) {
        this.isFire = isFire;
    }

    @Override
    public boolean isSneaking() {
        return isSneaking;
    }

    @Override
    public void setSneaking(boolean isCrounched) {
        this.isSneaking = isCrounched;
    }

    @Override
    public boolean isSprinting() {
        return isSprinting;
    }

    @Override
    public void setSprinting(boolean isSprinting) {
        this.isSprinting = isSprinting;
    }

    @Override
    public boolean isEating() {
        return isEating;
    }

    @Override
    public void setEating(boolean isEating) {
        this.isEating = isEating;
    }

    @Override
    public boolean isInvisible() {
        return isInvisible;
    }

    @Override
    public void setInvisible(boolean isInvisible) {
        this.isInvisible = isInvisible;
    }

    @Override
    public short getAirTicks() {
        return airTicks;
    }

    @Override
    public void setAirTicks(short ticks) {
        this.airTicks = ticks;
    }

    @Override
    public String getCustomName() {
        return customName;
    }

    @Override
    public void setCustomName(String customName) {
        this.customName = customName;
    }

    @Override
    public boolean isCustomNameVisible() {
        return isCustomNameVisible;
    }

    @Override
    public void setCustomNameVisible(boolean isCustomNameVisible) {
        this.isCustomNameVisible = isCustomNameVisible;
    }

    @Override
    public boolean isSilent() {
        return isSilent;
    }

    @Override
    public void setSilent(boolean isSilent) {
        this.isSilent = isSilent;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public EntityType getType() {
        return this.entityType;
    }

    @Override
    public Queue<Player> getViewers() {
        return viewers;
    }

    @Override
    public void addViewer(Player player) {
        viewers.add(player);
    }

    @Override
    public void removeViewer(Player player) {
        viewers.remove(player);
    }

}
