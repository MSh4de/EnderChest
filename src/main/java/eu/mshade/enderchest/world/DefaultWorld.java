package eu.mshade.enderchest.world;

import eu.mshade.enderchest.entity.EntityFactory;
import eu.mshade.enderchest.marshal.world.ChunkBinaryTagMarshal;
import eu.mshade.enderchest.marshal.world.WorldBinaryTagMarshal;
import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.EntityIdProvider;
import eu.mshade.enderframe.entity.EntityType;
import eu.mshade.enderframe.event.ChunkCreateEvent;
import eu.mshade.enderframe.event.ChunkLoadEvent;
import eu.mshade.enderframe.item.MaterialKey;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.enderframe.world.*;
import eu.mshade.enderframe.world.block.Block;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.enderframe.world.chunk.ChunkStateStore;
import eu.mshade.mwork.MWork;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.poet.BinaryTagPoet;
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
    private final WorldManager worldManager;

    private final Map<String, BinaryTagPoet> binaryTagPoetByRegion = new ConcurrentHashMap<>();
    private final Map<BinaryTagPoet, Queue<Chunk>> chunksByRegion = new ConcurrentHashMap<>();
    private final Map<BinaryTagPoet, Long> lastUsageRegion = new ConcurrentHashMap<>();
    private final Map<BinaryTagPoet, String> regionByBinaryTagPoet = new ConcurrentHashMap<>();
    private final BinaryTagDriver binaryTagDriver = MWork.get().getBinaryTagDriver();
    private final Map<Long, String> trackDuplicatedChunk = new ConcurrentHashMap<>();
    private final ChunkBinaryTagMarshal chunkBinaryTagMarshal;
    private final WorldBinaryTagMarshal worldBinaryTagMarshal;

    public DefaultWorld(WorldManager worldManager, File worldFolder, MetadataKeyValueBucket metadataKeyValueBucket) {
        super(worldFolder, metadataKeyValueBucket);
        this.worldManager = worldManager;
        this.regionFolder.mkdirs();
        this.indicesFolder.mkdirs();
        this.chunkBinaryTagMarshal = binaryTagDriver.getDynamicMarshal(ChunkBinaryTagMarshal.class);
        this.worldBinaryTagMarshal = binaryTagDriver.getDynamicMarshal(WorldBinaryTagMarshal.class);

    }

    public DefaultWorld(WorldManager worldManager, File worldFolder) {
        this(worldManager, worldFolder, new MetadataKeyValueBucket());
    }


    @Override
    public void saveChunk(Chunk chunk) {
        BinaryTagPoet binaryTagPoet = getBinaryTagPoet(binaryTagDriver, this, chunk);
        chunkBinaryTagMarshal.write(binaryTagDriver, binaryTagPoet, chunk);
        this.lastUsageRegion.put(binaryTagPoet, System.currentTimeMillis());

    }

    @Override
    public void flushChunk(Chunk chunk, boolean save) {
        ChunkStateStore chunkStateStore = chunk.getChunkStateStore();

        chunkStateStore.setFinishWrite(() -> {
            BinaryTagPoet binaryTagPoet = getBinaryTagPoet(binaryTagDriver, this, chunk);
            this.chunksByRegion.get(binaryTagPoet).remove(chunk);
            this.chunkById.remove(chunk.getId());
        });

        if (save && !chunkStateStore.isInChunkSafeguard()) {
            worldManager.getChunkSafeguard().addChunk(chunk);
            return;
        }

        if (!save) {
            chunkStateStore.finishWrite();
        }


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

        BinaryTagPoet binaryTagPoet = getBinaryTagPoet(binaryTagDriver, this, x, z);

        if (!containsChunk(binaryTagDriver, this, x, z)) {
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
            CompletableFuture<Chunk> completableFuture = new CompletableFuture<>();
            completableFuture.completeAsync(() -> {

                Chunk chunk;
                try {
                    chunk = chunkBinaryTagMarshal.read(binaryTagDriver, binaryTagPoet, this, x, z);
                    ChunkLoadEvent chunkLoadEvent = new ChunkLoadEvent(completableFuture);
                    EnderFrame.get().getEnderFrameEventBus().publish(chunkLoadEvent);

                    if (!chunkLoadEvent.isCancelled()) {
                        chunk.getChunkStateStore().setChunkStatus(ChunkStatus.LOADED);
                        chunkById.put(id, completableFuture);
                        this.chunksByRegion.computeIfAbsent(binaryTagPoet, k -> new ConcurrentLinkedQueue<>()).add(chunk);
                        this.lastUsageRegion.put(binaryTagPoet, System.currentTimeMillis());
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
    public boolean hasChunkBuffer(int x, int z) {
        return chunkById.containsKey(Chunk.key(x, z));
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
        EntityIdProvider entityIdManager = EntityIdProvider.INSTANCE;
        Location entityLocation = location.clone();

        try {
            int id = entityIdManager.getFreeId();
            Entity entity = entityFactory.factoryEntity(entityType, ParameterContainer.of()
                    .putContainer(id)
                    .putContainer(entityLocation));
            /*

            location.getChunkBuffer().addEntity(entity);
            location.getChunkBuffer().getViewers().stream()
                    .filter(player -> player.getLocation().distance(entityLocation) <= 64)
                    .forEach(entity::addViewer);

             */

            return entity;
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
    public Collection<BinaryTagPoet> getRegionBinaryTagPoets() {
        return this.binaryTagPoetByRegion.values();
    }

    @Override
    public void saveWorld() {
        worldBinaryTagMarshal.write(binaryTagDriver, this);
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
                        worldManager.getChunkSafeguard().addChunk(chunk);
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

            for (BinaryTagPoet binaryTagPoet : this.binaryTagPoetByRegion.values()) {

                long delay = System.currentTimeMillis() - this.lastUsageRegion.get(binaryTagPoet);
                if (delay > 5000 && this.chunksByRegion.get(binaryTagPoet).isEmpty()) {
                    this.lastUsageRegion.remove(binaryTagPoet);
                    this.chunksByRegion.remove(binaryTagPoet);
                    this.binaryTagPoetByRegion.remove(regionByBinaryTagPoet.remove(binaryTagPoet));
                    if (binaryTagPoet.getCompoundSectionIndex().consume()) {
                        binaryTagPoet.writeCompoundSectionIndex();
                    }
                }
            }


            saveWorld();
        }

        this.getChunks().forEach(chunkCompletableFuture -> {
            Chunk chunk = chunkCompletableFuture.join();
            if (chunk == null)
                return;

            chunk.getEntities().forEach(Entity::tick);
        });
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

    public boolean containsChunk(BinaryTagDriver binaryTagDriver, World world, int x, int z) {
        BinaryTagPoet binaryTagPoet = getBinaryTagPoet(binaryTagDriver, world, x, z);
        return binaryTagPoet.getCompoundSectionIndex().containsKey(chunkId(x, z));
    }

    private BinaryTagPoet getBinaryTagPoet(BinaryTagDriver binaryTagDriver, World world, int x, int z) {
        String regionId = regionId(x, z);
        return binaryTagPoetByRegion.computeIfAbsent(regionId, s -> {
            BinaryTagPoet binaryTagPoet = new BinaryTagPoet(new File(world.getIndicesFolder(), regionId + ".dat"), new File(world.getRegionFolder(), regionId + ".dat"), binaryTagDriver);
            this.chunksByRegion.put(binaryTagPoet, new ConcurrentLinkedQueue<>());
            this.regionByBinaryTagPoet.put(binaryTagPoet, s);
            this.lastUsageRegion.put(binaryTagPoet, System.currentTimeMillis());
            return binaryTagPoet;
        });
    }

    private BinaryTagPoet getBinaryTagPoet(BinaryTagDriver binaryTagDriver, World world, Chunk chunk) {
        return getBinaryTagPoet(binaryTagDriver, world, chunk.getX(), chunk.getZ());
    }

    @Override
    public String toString() {
        return "DefaultWorld{" +
                "name=" + getName() +
                '}';
    }
}
