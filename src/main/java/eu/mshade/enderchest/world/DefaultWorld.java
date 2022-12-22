package eu.mshade.enderchest.world;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderchest.entity.EntityFactory;
import eu.mshade.enderchest.marshal.world.ChunkBinaryTagMarshal;
import eu.mshade.enderchest.marshal.world.WorldBinaryTagMarshal;
import eu.mshade.enderframe.Agent;
import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.Watchable;
import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.EntityType;
import eu.mshade.enderframe.event.ChunkCreateEvent;
import eu.mshade.enderframe.event.ChunkLoadEvent;
import eu.mshade.enderframe.event.ChunkUnloadEvent;
import eu.mshade.enderframe.inventory.Inventory;
import eu.mshade.enderframe.inventory.InventoryRepository;
import eu.mshade.enderframe.item.MaterialKey;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.enderframe.world.*;
import eu.mshade.enderframe.world.block.Block;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.enderframe.world.chunk.ChunkStateStore;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.segment.SegmentBinaryTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

public class DefaultWorld extends World {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWorld.class);
    private final ChunkSafeguard chunkSafeguard;

    private final Map<String, SegmentBinaryTag> binaryTagPoetByRegion = new ConcurrentHashMap<>();
    private final Map<SegmentBinaryTag, Queue<Chunk>> chunksByRegion = new ConcurrentHashMap<>();
    private final Map<SegmentBinaryTag, Long> lastUsageRegion = new ConcurrentHashMap<>();
    private final Map<SegmentBinaryTag, String> regionByBinaryTagPoet = new ConcurrentHashMap<>();
    private final BinaryTagDriver binaryTagDriver = EnderFrame.get().getBinaryTagDriver();

    public DefaultWorld(ChunkSafeguard chunkSafeguard,  File worldFolder, MetadataKeyValueBucket metadataKeyValueBucket) {
        super(worldFolder, metadataKeyValueBucket);
        this.chunkSafeguard = chunkSafeguard;
        this.regionFolder.mkdirs();
        this.indicesFolder.mkdirs();

    }

    public DefaultWorld(ChunkSafeguard chunkSafeguard, File worldFolder) {
        this(chunkSafeguard, worldFolder, new MetadataKeyValueBucket(true));
    }


    @Override
    public void flushChunk(Chunk chunk, boolean save) {
        ChunkStateStore chunkStateStore = chunk.getChunkStateStore();

        chunkStateStore.setFinishWrite(() -> {
            SegmentBinaryTag segmentBinaryTag = getCarbonBinaryTag(binaryTagDriver, this, chunk);
            this.chunksByRegion.get(segmentBinaryTag).remove(chunk);
            this.chunkById.remove(chunk.getId());
            EnderFrame.get().getEnderFrameEventBus().publish(new ChunkUnloadEvent(chunk));

            Agent agent = chunk.getAgent();
            for (Watchable watchable : agent.getWatchings()) {
                if (watchable instanceof Inventory inventory) {
                    agent.leaveWatch(inventory);
                    InventoryRepository.INSTANCE.remove(inventory);
                }
            }

        });

        if (save && !chunkStateStore.isInChunkSafeguard()) {
            chunkSafeguard.addChunk(chunk);
            return;
        }

        if (!save) {
            chunkStateStore.finishWrite();
        }

    }

    @Override
    public void saveChunk(Chunk chunk) {
        SegmentBinaryTag segmentBinaryTag = getCarbonBinaryTag(binaryTagDriver, this, chunk);
        ChunkBinaryTagMarshal.INSTANCE.write(segmentBinaryTag, chunk, EnderChest.INSTANCE.getMetadataKeyValueBufferRegistry());
        this.lastUsageRegion.put(segmentBinaryTag, System.currentTimeMillis());

    }

    @Override
    public CompletableFuture<Chunk> getChunk(int x, int z) {

        final long id = Chunk.key(x, z);

        CompletableFuture<Chunk> chunkCompletableFuture = chunkById.get(id);
        if (chunkCompletableFuture != null) {
            Chunk chunk = chunkCompletableFuture.join();
            if (chunk != null) {
                ChunkStateStore chunkStateStore = chunk.getChunkStateStore();
                if (chunkStateStore.getChunkStatus() != ChunkStatus.LOADED) chunkStateStore.setChunkStatus(ChunkStatus.LOADED);
            }
            return chunkCompletableFuture;
        }


        if (!isChunkExists(x, z)) {
            CompletableFuture<Chunk> completableFuture = new CompletableFuture<>();
            completableFuture.completeAsync(() -> {
                DefaultChunk chunk = new DefaultChunk(x, z, this);
                ChunkCreateEvent chunkCreateEvent = new ChunkCreateEvent(chunk);
                EnderFrame.get().getEnderFrameEventBus().publish(chunkCreateEvent);
                if (!chunkCreateEvent.isCancelled()) {
                    if (getChunkGenerator() != null) getChunkGenerator().generate(chunk);
                    chunk.getChunkStateStore().setChunkStatus(ChunkStatus.LOADED);
                    chunkById.put(id, completableFuture);
                    return chunk;
                }
                return new EmptyChunk(x, z, this);
            });

            return completableFuture;
        } else {
            SegmentBinaryTag segmentBinaryTag = getCarbonBinaryTag(binaryTagDriver, this, x, z);
            CompletableFuture<Chunk> completableFuture = new CompletableFuture<>();
            completableFuture.completeAsync(() -> {

                Chunk chunk;
                try {
                    chunk = ChunkBinaryTagMarshal.INSTANCE.read(segmentBinaryTag, this, x, z, EnderChest.INSTANCE.getMetadataKeyValueBufferRegistry());
                    ChunkLoadEvent chunkLoadEvent = new ChunkLoadEvent(completableFuture);
                    EnderFrame.get().getEnderFrameEventBus().publish(chunkLoadEvent);

                    if (!chunkLoadEvent.isCancelled()) {
                        chunk.getChunkStateStore().setChunkStatus(ChunkStatus.LOADED);
                        chunkById.put(id, completableFuture);
                        this.chunksByRegion.computeIfAbsent(segmentBinaryTag, k -> new ConcurrentLinkedQueue<>()).add(chunk);
                        this.lastUsageRegion.put(segmentBinaryTag, System.currentTimeMillis());
                        return chunk;

                    }
                } catch (Exception e) {
                    LOGGER.error("Can't read chunk x=" + x + ", z=" + z + " from region=" + regionId(x, z), e);
                }

                return new EmptyChunk(x, z, this);
            });
            return completableFuture;
        }

    }


    @Override
    public CompletableFuture<Chunk> getChunk(long id) {
        return chunkById.get(id);
    }


    @Override
    public boolean hasChunkLoaded(int x, int z) {
        return chunkById.containsKey(Chunk.key(x, z));
    }

    @Override
    public boolean isChunkExists(int chunkX, int chunkZ) {
        SegmentBinaryTag segmentBinaryTag = getCarbonBinaryTag(binaryTagDriver, this, chunkX, chunkZ);
        return segmentBinaryTag.getCompoundSectionIndex().containsKey(chunkId(chunkX, chunkZ));
    }


    @Override
    public void addEntity(Entity entity) {
        if (this.entities.contains(entity)) return;
        this.entities.add(entity);
    }

    @Override
    public void removeEntity(Entity entity) {
        this.entities.remove(entity);
    }

    @Override
    public Entity spawnEntity(EntityType entityType, Location location) {
        if (location == null)
            throw new NullPointerException("Location cannot be null when trying to spawn an entity.");

        EntityFactory entityFactory = EntityFactory.get();
        Location entityLocation = location.clone();

        try {

            /*

            location.getChunkBuffer().addEntity(entity);
            location.getChunkBuffer().getViewers().stream()
                    .filter(player -> player.getLocation().distance(entityLocation) <= 64)
                    .forEach(entity::addViewer);

             */

            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void setBlock(int x, int y, int z, MaterialKey materialKey) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        try {
            Chunk chunk = getChunk(chunkX, chunkZ).get();
            chunk.setBlock(x, y, z, materialKey.toBlock());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setBlock(int x, int y, int z, Block block) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        try {
            Chunk chunk = getChunk(chunkX, chunkZ).get();
            chunk.setBlock(x, y, z, block);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        try {
            Chunk chunk = getChunk(chunkX, chunkZ).get();
            return chunk.getBlock(x, y, z);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<SegmentBinaryTag> getRegions() {
        return this.binaryTagPoetByRegion.values();
    }

    @Override
    public void saveWorld() {
        WorldBinaryTagMarshal.INSTANCE.write(binaryTagDriver, this, EnderChest.INSTANCE.getMetadataKeyValueBufferRegistry());
    }

    @Override
    public void tick() {
        if (this.isPeriod(20)) {
            for (CompletableFuture<Chunk> chunkCompletableFuture : this.getChunks()) {
                Chunk chunk = chunkCompletableFuture.join();
                if (chunk == null) continue;
                ChunkStateStore chunkStateStore = chunk.getChunkStateStore();

                if (chunkStateStore.getChunkStatus() == ChunkStatus.LOADED) {
                    if (chunkStateStore.addAndGetAge(1) % 30 == 0 && !chunkStateStore.isInChunkSafeguard() && chunkStateStore.isAutoSaveEnabled()) {
                        chunkSafeguard.addChunk(chunk);
                        chunkStateStore.resetAge();
                    }

                    if (chunk.getWatchers().isEmpty() && chunkStateStore.outdatedInteract(500)) {
                        chunkStateStore.setChunkStatus(ChunkStatus.PREPARE_TO_UNLOAD);
                    }
                }

                if ((chunkStateStore.getChunkStatus() == ChunkStatus.PREPARE_TO_UNLOAD && chunkStateStore.outdatedChunkStatus(2000)) || (chunkStateStore.getChunkStatus() == ChunkStatus.PREPARE_TO_LOAD && chunkStateStore.outdatedChunkStatus(ChunkStatus.PREPARE_TO_LOAD, 10000))) {
                    this.flushChunk(chunk);
                }
            }

            for (SegmentBinaryTag segmentBinaryTag : this.binaryTagPoetByRegion.values()) {

                long delay = System.currentTimeMillis() - this.lastUsageRegion.get(segmentBinaryTag);
                if (delay > 5000 && this.chunksByRegion.get(segmentBinaryTag).isEmpty()) {
                    this.lastUsageRegion.remove(segmentBinaryTag);
                    this.chunksByRegion.remove(segmentBinaryTag);
                    this.binaryTagPoetByRegion.remove(regionByBinaryTagPoet.remove(segmentBinaryTag));
                    if (segmentBinaryTag.getCompoundSectionIndex().consume()) {
                        segmentBinaryTag.writeCompoundSectionIndex();
                    }
                }
            }

            saveWorld();
        }
    }

    private String regionId(Chunk chunk) {
        return regionId(chunk.getX(), chunk.getZ());
    }

    private String regionId(int chunkX, int chunkZ) {
        return (chunkX >> 5) + "," + (chunkZ >> 5);
    }

    private String chunkId(Chunk chunk) {
        return chunk.getX() + "," + chunk.getZ();
    }

    private String chunkId(int chunkX, int chunkZ) {
        return chunkX + "," + chunkZ;
    }


    private SegmentBinaryTag getCarbonBinaryTag(BinaryTagDriver binaryTagDriver, World world, int x, int z) {
        String regionId = regionId(x, z);
        return binaryTagPoetByRegion.computeIfAbsent(regionId, s -> {
            SegmentBinaryTag segmentBinaryTag = new SegmentBinaryTag(new File(world.getIndicesFolder(), regionId + ".dat"), new File(world.getRegionFolder(), regionId + ".dat"), binaryTagDriver);
            this.chunksByRegion.put(segmentBinaryTag, new ConcurrentLinkedQueue<>());
            this.regionByBinaryTagPoet.put(segmentBinaryTag, s);
            this.lastUsageRegion.put(segmentBinaryTag, System.currentTimeMillis());
            return segmentBinaryTag;
        });
    }

    private SegmentBinaryTag getCarbonBinaryTag(BinaryTagDriver binaryTagDriver, World world, Chunk chunk) {
        return getCarbonBinaryTag(binaryTagDriver, world, chunk.getX(), chunk.getZ());
    }

    @Override
    public String toString() {
        return "DefaultWorld{" +
                "name=" + getName() +
                '}';
    }
}
