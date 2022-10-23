package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.Watchable;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.entity.Projectile;
import eu.mshade.enderframe.entity.metadata.FlyingEntityMetadata;
import eu.mshade.enderframe.entity.metadata.SprintingEntityMetadata;
import eu.mshade.enderframe.metadata.entity.EntityMetadataKey;
import eu.mshade.enderframe.mojang.chat.ChatColor;
import eu.mshade.enderframe.mojang.chat.TextPosition;
import eu.mshade.enderframe.protocol.SessionWrapper;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;
import eu.mshade.enderframe.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
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

            if (hasChangeChunk) {
                Location location = this.getLocation();
                World world = location.getWorld();

                int radiusMax = 10;
                //int radius = (int) Math.max(3, Math.round(radiusMax - (memoryEfficiency * radiusMax)));


                int radius = 10;
                //int radius = (int) Math.max(3, Math.round(radiusMax - (speed / vMax)));

                if (hasChangeChunk) {
                    lastUpdateByChangeChunk = true;
                    updateChunkBySpeed = false;
                }

                if (hasChangeSpeedInChunk) {
                    lastUpdateByChangeChunk = false;
                    updateChunkBySpeed = true;
                    lastSpeedInChunk = speed;
                }

                long start = System.currentTimeMillis();
                Queue<CompletableFuture<Chunk>> askChunks = new ConcurrentLinkedQueue<>();

                int rSquared = radius * radius;
                this.lastServerChunkLocation = this.getLocation().clone();

                int chunkX = location.getChunkX();
                int chunkZ = location.getChunkZ();


                for (int x = chunkX - radius; x <= chunkX + radius; x++) {
                    for (int z = chunkZ - radius; z <= chunkZ + radius; z++) {
                        if ((chunkX - x) * (chunkX - x) + (chunkZ - z) * (chunkZ - z) <= rSquared) {
                            CompletableFuture<Chunk> chunk = world.getChunk(x, z);
                            askChunks.add(chunk);
                        }
                    }
                }

                try {
                    Void waitingAskChunk = CompletableFuture.allOf(askChunks.toArray(new CompletableFuture[0])).get();

                    Queue<Chunk> result = askChunks.stream().map(CompletableFuture::join).distinct().collect(Collectors.toCollection(ConcurrentLinkedQueue::new));

                    for (Chunk chunk : this.getLookAtChunks()) {
                        if (!result.contains(chunk)) {
                            this.getLookAtChunks().remove(chunk);
                            this.getSessionWrapper().sendUnloadChunk(chunk);
                            chunk.removeWatcher(this);
                        }
                    }
                    Queue<Chunk> newChunks = new ConcurrentLinkedQueue<>();
                    result.stream().filter(chunk -> !hasLookAtChunk(chunk)).forEach(chunk -> {
                        this.getLookAtChunks().add(chunk);
                        chunk.addWatcher(this);
                        newChunks.add(chunk);
                    });

                    List<CompletableFuture<Void>> sendingChunk = new ArrayList<>();
                    newChunks.forEach(chunk -> {
                        CompletableFuture<Void> runnableCompletableFuture = new CompletableFuture<>();
                        sendingChunk.add(runnableCompletableFuture);
                        runnableCompletableFuture.completeAsync(() -> {
                            this.getSessionWrapper().sendChunk(chunk);
                            return null;
                        });
                    });


                    Void waitingSendingChunk = CompletableFuture.allOf(sendingChunk.toArray(new CompletableFuture[0]))
                            .exceptionally(throwable -> {
                                LOGGER.error("Error while sending chunk", throwable);
                                return null;
                            }).get();

                    this.getSessionWrapper().sendMessage(ChatColor.GREEN + "Chunk loaded in " + (System.currentTimeMillis() - start) + "ms, there are "+ world.getChunks().size()+" chunks in all, new chunk "+sendingChunk.size() , TextPosition.HOT_BAR);
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error("", e);
                }


                /*
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

                 */
            }

        }




        if (isPeriod(20)){
            this.getSessionWrapper().sendKeepAlive((int) System.currentTimeMillis());
        }
    }



    @Override
    public String toString() {
        return "DefaultPlayer{" +
                "sessionWrapper=" + sessionWrapper +
                '}';
    }

    @Override
    public String getAgent() {
        return this.getName();
    }

    @Override
    public void joinWatch(Watchable watchable) {
        watchable.addWatcher(this);
    }

    @Override
    public void leaveWatch(Watchable watchable) {
        watchable.removeWatcher(this);
    }
}
