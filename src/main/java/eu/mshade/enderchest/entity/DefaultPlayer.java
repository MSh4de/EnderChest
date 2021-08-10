package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.entity.EntityType;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.mojang.SkinParts;
import eu.mshade.enderframe.protocol.ProtocolVersion;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;
import eu.mshade.enderman.packet.play.PacketOutEntityTeleport;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultPlayer extends Player {

    private EnderFrameSessionHandler enderFrameSessionHandler;
    private Location location;
    private Vector velocity;
    private final int entityId;
    private boolean isFire;
    private boolean isCrounched;
    private boolean isSprinting;
    private boolean isEating;
    private boolean isInvisible;
    private short airTicks;
    private String customName;
    private boolean isCustomNameVisible;
    private boolean isSilent;
    private final UUID uuid;
    private final Queue<Player> viewers = new ConcurrentLinkedQueue<>();
    private float health;
    private int potionEffectColor;
    private boolean isPotionEffectAmbient;
    private byte numberOfArrowsInEntity;
    private boolean isAIDisable;
    private final String name;
    private InetSocketAddress inetSocketAddress;
    private ProtocolVersion protocolVersion;
    private int ping;
    private SkinParts skinParts;
    private boolean unused;
    private float absorptionHearts;
    private int score;

    public DefaultPlayer(int entityId, Location location, EnderFrameSessionHandler enderFrameSessionHandler) {
        this.entityId = entityId;
        this.location = location;
        this.name = enderFrameSessionHandler.getEnderFrameSession().getGameProfile().getName();
        this.enderFrameSessionHandler = enderFrameSessionHandler;
        this.uuid = enderFrameSessionHandler.getEnderFrameSession().getGameProfile().getId();
    }

    public DefaultPlayer(Location location, Vector velocity, int entityId, boolean isFire, boolean isSneaking, boolean isSprinting, boolean isEating, boolean isInvisible, short airTicks, String customName, boolean isCustomNameVisible, boolean isSilent, UUID uuid, float health, int potionEffectColor, boolean isPotionEffectAmbient, byte numberOfArrowsInEntity, boolean isAIDisable, String name, InetSocketAddress inetSocketAddress, ProtocolVersion protocolVersion, int ping, SkinParts skinParts, boolean unused, float absorptionHearts, int score) {
        this.location = location;
        this.velocity = velocity;
        this.entityId = entityId;
        this.isFire = isFire;
        this.isCrounched = isSneaking;
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
        this.name = name;
        this.inetSocketAddress = inetSocketAddress;
        this.protocolVersion = protocolVersion;
        this.ping = ping;
        this.skinParts = skinParts;
        this.unused = unused;
        this.absorptionHearts = absorptionHearts;
        this.score = score;
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
        return isCrounched;
    }

    @Override
    public void setSneaking(boolean isCrounched) {
        this.isCrounched = isCrounched;
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
        return EntityType.PLAYER;
    }

    @Override
    public Queue<Player> getViewers() {
        return viewers;
    }

    @Override
    public void addViewer(Player player) {
        enderFrameSessionHandler.getEnderFrameSession().sendPlayer(player);
        viewers.add(player);
        enderFrameSessionHandler.sendPacket(new PacketOutEntityTeleport(player.getEntityId(),player,false));
    }

    @Override
    public void removeViewer(Player player) {
        enderFrameSessionHandler.getEnderFrameSession().removeEntities(player);
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
    public EnderFrameSessionHandler getEnderFrameSessionHandler() {
        return this.enderFrameSessionHandler;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public SocketAddress getSocketAddress() {
        return this.enderFrameSessionHandler.getEnderFrameSession().getSocketAddress();
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return this.protocolVersion;
    }

    @Override
    public int getPing() {
        return this.ping;
    }

    @Override
    public SkinParts getSkinParts() {
        return this.skinParts;
    }

    @Override
    public boolean unused() {
        return this.unused;
    }

    @Override
    public float getAbsorptionHearts() {
        return this.absorptionHearts;
    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultPlayer that = (DefaultPlayer) o;
        return Objects.equals(enderFrameSessionHandler, that.enderFrameSessionHandler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enderFrameSessionHandler);
    }
}
