package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.EntityType;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.entity.Zombie;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultZombie extends Zombie {

    private Location location;
    private Vector velocity = new Vector();
    private int entityId;
    private boolean isFire;
    private boolean isSneaking;
    private boolean isSprinting;
    private boolean isEating;
    private boolean isInvisible;
    private short airTicks;
    private String customName = "";
    private boolean isCustomNameVisible;
    private boolean isSilent;
    private UUID uuid;
    private final EntityType entityType = EntityType.ZOMBIE;
    private final Queue<Player> viewers = new ConcurrentLinkedQueue<>();
    private float health;
    private int potionEffectColor;
    private boolean isPotionEffectAmbient;
    private byte numberOfArrowsInEntity;
    private boolean isAIDisable;
    private boolean isChild;
    private boolean isVillager;
    private boolean isConverting;

    public DefaultZombie(int entityId, Location location) {
        this.location = location;
        this.uuid = UUID.randomUUID();
        this.entityId = entityId;
    }

    public DefaultZombie(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowsInEntity, boolean isAIDisable) {
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
        this.health = health;
        this.potionEffectColor = potionEffectColor;
        this.isPotionEffectAmbient = isPotionEffectAmbient;
        this.numberOfArrowsInEntity = numberOfArrowsInEntity;
        this.isAIDisable = isAIDisable;
    }

    public DefaultZombie(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowsInEntity, boolean isAIDisable, boolean isChild, boolean isVillager, boolean isConverting) {
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
        this.health = health;
        this.potionEffectColor = potionEffectColor;
        this.isPotionEffectAmbient = isPotionEffectAmbient;
        this.numberOfArrowsInEntity = numberOfArrowsInEntity;
        this.isAIDisable = isAIDisable;
        this.isChild = isChild;
        this.isVillager = isVillager;
        this.isConverting = isConverting;
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
        return EntityType.ZOMBIE;
    }

    @Override
    public Queue<Player> getViewers() {
        return viewers;
    }

    @Override
    public void addViewer(Player player) {
        player.getEnderFrameSessionHandler().getEnderFrameSession().sendMob(this);
        viewers.add(player);
        player.getEnderFrameSessionHandler().getEnderFrameSession().sendTeleport(this,false);
    }

    @Override
    public void removeViewer(Player player) {
        player.getEnderFrameSessionHandler().getEnderFrameSession().removeEntities(player);
        viewers.remove(player);
    }

    @Override
    public float getHealth() {
        return health;
    }

    @Override
    public void setHealth(float health) {
        this.health = health;
    }

    @Override
    public int getPotionEffectColor() {
        return potionEffectColor;
    }

    @Override
    public void setPotionEffectColor(int color) {
        this.potionEffectColor = color;
    }

    @Override
    public boolean isPotionEffectAmbient() {
        return isPotionEffectAmbient;
    }

    @Override
    public void setPotionEffectAmbient(boolean ambient) {
        this.isPotionEffectAmbient = ambient;
    }

    @Override
    public byte getNumberOfArrowInEntity() {
        return numberOfArrowsInEntity;
    }

    @Override
    public void setNumberOfArrowInEntity(byte b) {
        this.numberOfArrowsInEntity = b;
    }

    @Override
    public boolean isAIDisable() {
        return isAIDisable;
    }

    @Override
    public void setAIDisable(boolean isDisable) {
        this.isAIDisable = isDisable;
    }

    @Override
    public boolean isChild() {
        return isChild;
    }

    @Override
    public void setChild(boolean isChild) {
        this.isChild = isChild;
    }

    @Override
    public boolean isVillager() {
        return isVillager;
    }

    @Override
    public void setVillager(boolean isVillager) {
        this.isVillager = isVillager;
    }

    @Override
    public boolean isConverting() {
        return isConverting;
    }

    @Override
    public void setConverting(boolean isConverting) {
        this.isConverting = isConverting;
    }

    @Override
    public String toString() {
        return "DefaultZombie{" +
                "location=" + location +
                ", velocity=" + velocity +
                ", entityId=" + entityId +
                ", isFire=" + isFire +
                ", isSneaking=" + isSneaking +
                ", isSprinting=" + isSprinting +
                ", isEating=" + isEating +
                ", isInvisible=" + isInvisible +
                ", airTicks=" + airTicks +
                ", customName='" + customName + '\'' +
                ", isCustomNameVisible=" + isCustomNameVisible +
                ", isSilent=" + isSilent +
                ", uuid=" + uuid +
                ", entityType=" + entityType +
                ", viewers=" + viewers +
                ", health=" + health +
                ", potionEffectColor=" + potionEffectColor +
                ", isPotionEffectAmbient=" + isPotionEffectAmbient +
                ", numberOfArrowsInEntity=" + numberOfArrowsInEntity +
                ", isAIDisable=" + isAIDisable +
                ", isChild=" + isChild +
                ", isVillager=" + isVillager +
                ", isConverting=" + isConverting +
                '}';
    }
}
