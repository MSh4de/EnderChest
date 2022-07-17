package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.entity.Projectile;
import eu.mshade.enderframe.entity.metadata.FlyingEntityMetadata;
import eu.mshade.enderframe.entity.metadata.SprintingEntityMetadata;
import eu.mshade.enderframe.metadata.entity.EntityMetadataKey;
import eu.mshade.enderframe.protocol.SessionWrapper;
import eu.mshade.enderframe.world.Chunk;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;
import eu.mshade.enderframe.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class DefaultPlayer extends Player {

    private static Logger LOGGER = LoggerFactory.getLogger(DefaultPlayer.class);

    private final SessionWrapper sessionWrapper;
    private Location lastServerChunkLocation;
    private double speed, lastSpeedInChunk;
    private boolean updateChunkBySpeed, lastUpdateByChangeChunk;

    private long lastElapsedTick;

    public DefaultPlayer(Location location, Vector velocity, int entityId, UUID uuid, SessionWrapper sessionWrapper) {
        super(location, velocity, entityId, uuid);
        this.setGameProfile(sessionWrapper.getGameProfile());
        this.setDisplayName(getGameProfile().getName());
        this.setMinecraftProtocolVersion(sessionWrapper.getHandshake().getVersion());
        this.sessionWrapper = sessionWrapper;
    }

    public DefaultPlayer(Location location, int entityId, SessionWrapper sessionWrapper) {
        this(location, new Vector(), entityId, sessionWrapper.getGameProfile().getId(), sessionWrapper);
    }

    @Override
    public SessionWrapper getSessionWrapper() {
        return this.sessionWrapper;
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile) {
        return null;
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile, Vector vector) {
        return null;
    }

    /**
     * Fix le calcul de speed en passant par un elapsedTime et non par une valeur fix(0,05)
     */

    @Override
    public void tick() {

        if (isPeriod(1)) {
            long now = System.currentTimeMillis();
            speed = this.getBeforeServerLocation().distanceXZ(getServerLocation()) / ((now - lastElapsedTick) * 1E-3);
            lastElapsedTick = now;
            setServerLocation(getLocation());


            getLookAtEntity().forEach(entity -> {
                getSessionWrapper().sendUpdateLocation(entity, entity.getBeforeServerLocation(), entity.getServerLocation());
            });


            boolean sprinting = this.getMetadataKeyValueBucket().getMetadataKeyValueOrDefault(EntityMetadataKey.SPRINTING, SprintingEntityMetadata.DEFAULT).getMetadataValue();
            double vMax = this.getMetadataKeyValueBucket().getMetadataKeyValueOrDefault(EntityMetadataKey.FLYING, FlyingEntityMetadata.DEFAULT).getMetadataValue() ? (sprinting ? 78.40 : 39.20) : (sprinting ? 20.20 : 15.54);

            boolean hasChangeChunk =  lastServerChunkLocation == null || (this.lastServerChunkLocation.getChunkX() != this.getLocation().getChunkX() || this.lastServerChunkLocation.getChunkZ() != this.getLocation().getChunkZ());
            boolean hasChangeSpeedInChunk = lastServerChunkLocation == null || (this.lastServerChunkLocation.getChunkX() == this.getLocation().getChunkX() && this.lastServerChunkLocation.getChunkZ() == this.getLocation().getChunkZ() && !updateChunkBySpeed && lastUpdateByChangeChunk && (lastSpeedInChunk >= speed));

            if (hasChangeChunk || hasChangeSpeedInChunk) {
                Location location = this.getLocation();

                int radius = (int) Math.max(3, Math.round(10 - (speed / vMax)));

                if (hasChangeChunk) {
                    lastUpdateByChangeChunk = true;
                    updateChunkBySpeed = false;
                }

                if (hasChangeSpeedInChunk) {
                    lastUpdateByChangeChunk = false;
                    updateChunkBySpeed = true;
                    lastSpeedInChunk = speed;
                }


                World world = location.getWorld();
                Queue<Chunk> result = new ConcurrentLinkedQueue<>();

                int rSquared = radius * radius;
                this.lastServerChunkLocation = this.getLocation().clone();

                int chunkX = location.getChunkX();
                int chunkZ = location.getChunkZ();


                for (int x = chunkX - radius; x <= chunkX + radius; x++) {
                    for (int z = chunkZ - radius; z <= chunkZ + radius; z++) {
                        if ((chunkX - x) * (chunkX - x) + (chunkZ - z) * (chunkZ - z) <= rSquared) {
                            Chunk chunk = world.getChunk(x, z);
                            if (chunk != null) {
                                result.add(chunk);
                            }
                        }
                    }
                }

                result.stream().filter(chunk -> !hasLookAtChunk(chunk)).forEach(chunk -> {
                    this.getLookAtChunks().add(chunk);
                    this.getSessionWrapper().sendChunk(chunk);
                    chunk.getViewers().add(this);
                });

                for (Chunk chunk : this.getLookAtChunks()) {
                    if (!result.contains(chunk)) {
                        this.getLookAtChunks().remove(chunk);
                        this.getSessionWrapper().sendUnloadChunk(chunk);
                        chunk.getViewers().remove(this);
                    }
                }


                Set<Entity> entities = new HashSet<>();

                for (int x = chunkX - 5; x <= chunkX + 5; x++) {
                    for (int z = chunkZ - 5; z <= chunkZ + 5; z++) {
                        if ((chunkX - x) * (chunkX - x) + (chunkZ - z) * (chunkZ - z) <= 5 * 5) {
                            Chunk chunkBuffer = world.getChunk(x, z);
                            entities.addAll(chunkBuffer.getEntities());
                            chunkBuffer.getViewers().stream().filter(target -> !target.equals(this)).forEach(entities::add);
                        }
                    }
                }

                Set<Entity> collect = entities.stream().filter(entity -> entity.getLocation().distanceXZ(this.getLocation()) <= 80).collect(Collectors.toSet());

                for (Entity entity : collect) {
                    if (!containsLookAtEntity(entity)) {
                        getSessionWrapper().sendEntity(entity);
                        this.addLookAtEntity(entity);
                    }
                }


                getLookAtEntity().stream().filter(entity -> !collect.contains(entity)).forEach(entity -> {
                    this.removeLookAtEntity(entity);
                    getSessionWrapper().removeEntity(entity);
                });
            }

        }




        if (isPeriod(20)){
            this.getSessionWrapper().sendKeepAlive((int) System.currentTimeMillis());

        }
    }

}
