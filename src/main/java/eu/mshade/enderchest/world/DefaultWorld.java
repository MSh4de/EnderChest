package eu.mshade.enderchest.world;

import eu.mshade.enderchest.entity.EntityFactory;
import eu.mshade.enderchest.marshal.world.ChunkBinaryTagMarshal;
import eu.mshade.enderchest.marshal.world.WorldBinaryTagMarshal;
import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.EntityIdManager;
import eu.mshade.enderframe.entity.EntityType;
import eu.mshade.enderframe.event.ChunkLoadEvent;
import eu.mshade.enderframe.item.MaterialKey;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.enderframe.metadata.world.WorldMetadataType;
import eu.mshade.enderframe.world.*;
import eu.mshade.mwork.MWork;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.poet.BinaryTagPoet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultWorld extends World {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWorld.class);
    private final WorldManager worldManager;

    private final Map<String, BinaryTagPoet> binaryTagPoetByRegion = new ConcurrentHashMap<>();

    private final Map<BinaryTagPoet, Queue<Chunk>> chunksByRegion = new ConcurrentHashMap<>();

    private final Map<BinaryTagPoet, Long> lastUsageRegion = new ConcurrentHashMap<>();
    private final Map<BinaryTagPoet, String> regionByBinaryTagPoet = new ConcurrentHashMap<>();
    private BinaryTagDriver binaryTagDriver = MWork.get().getBinaryTagDriver();
    private ChunkBinaryTagMarshal chunkBinaryTagMarshal;
    private WorldBinaryTagMarshal worldBinaryTagMarshal;

    public DefaultWorld(WorldManager worldManager, File worldFolder, MetadataKeyValueBucket<WorldMetadataType> metadataKeyValueBucket){
        super(worldFolder, metadataKeyValueBucket);
        this.worldManager = worldManager;
        this.regionFolder.mkdirs();
        this.indicesFolder.mkdirs();
        this.chunkBinaryTagMarshal = binaryTagDriver.getDynamicMarshal(ChunkBinaryTagMarshal.class);
        this.worldBinaryTagMarshal = binaryTagDriver.getDynamicMarshal(WorldBinaryTagMarshal.class);

    }
    public DefaultWorld(WorldManager worldManager, File worldFolder) {
        this(worldManager, worldFolder, new MetadataKeyValueBucket<>());
    }



    @Override
    public void flushChunk(Chunk chunk, boolean save) {
        //this.chunkFiles.computeIfAbsent(chunkBuffer.getId(), integer -> chunkBuffer.getFile());

        /*
        ChunkUnloadEvent chunkUnloadEvent = new ChunkUnloadEvent(chunk);
        EnderFrame.get().getEnderFrameEventBus().publish(chunkUnloadEvent);

        if (!chunkUnloadEvent.isCancelled()) {

        }

         */
        BinaryTagPoet binaryTagPoet = getBinaryTagPoet(binaryTagDriver, this, chunk);
        if (save && chunk.hasChange()) {
            chunkBinaryTagMarshal.write(binaryTagDriver, binaryTagPoet, chunk);
            this.lastUsageRegion.put(binaryTagPoet, System.currentTimeMillis());
        }

        this.chunksByRegion.get(binaryTagPoet).remove(chunk);

        this.chunks.remove(chunk.getId());
        chunk.clearEntities();

    }

    @Override
    public Chunk getChunk(int x, int z) {

        final UUID id = Chunk.ofId(x, z);
        Chunk chunk = chunks.get(id);
        if (chunk != null) {
            ChunkLoadEvent chunkLoadEvent = new ChunkLoadEvent(chunk);
            EnderFrame.get().getEnderFrameEventBus().publish(chunkLoadEvent);
            if (!chunkLoadEvent.isCancelled()) {
                return chunk;
            }
        }

        BinaryTagPoet binaryTagPoet = getBinaryTagPoet(binaryTagDriver, this, x, z);

        if (!containsChunk(binaryTagDriver, this, x, z) && !chunks.containsKey(id)) {
            chunk = new DefaultChunk(x, z, true, this);
            ChunkLoadEvent chunkLoadEvent = new ChunkLoadEvent(chunk);
            EnderFrame.get().getEnderFrameEventBus().publish(chunkLoadEvent);
            if (!chunkLoadEvent.isCancelled()) {
                getChunkGenerator().generate(chunk);
                chunks.put(id, chunk);
                return chunk;
            }
        } else {
            try {
                chunk = chunkBinaryTagMarshal.read(binaryTagDriver, binaryTagPoet, this, x, z);
                this.chunksByRegion.get(binaryTagPoet).add(chunk);
                this.lastUsageRegion.put(binaryTagPoet, System.currentTimeMillis());
                //chunk = worldManager.getWorldBufferIO().readChunkBuffer(this, worldManager, file);
                ChunkLoadEvent chunkLoadEvent = new ChunkLoadEvent(chunk);
                EnderFrame.get().getEnderFrameEventBus().publish(chunkLoadEvent);

                if (!chunkLoadEvent.isCancelled()) {
                    chunks.put(id, chunk);

                    return chunk;

                }

            }catch (Exception e) {
                System.out.println("ERROR, CHUNK CORRUPTED, "+x+", "+z);
                //binaryTagPoet.getCompoundSectionIndex().removeSectionIndices(chunkId(x, z));
            }
        }

        return null;
    }


    @Override
    public Chunk getChunk(UUID id) {
        return chunks.get(id);
    }

    @Override
    public void addChunkBuffer(Chunk chunk) {
        this.chunks.put(chunk.getId(), chunk);
    }



    @Override
    public boolean hasChunkBuffer(int x, int z) {
        return chunks.containsKey(Chunk.ofId(x, z));
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
        EntityIdManager entityIdManager = EntityIdManager.get();
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
    public Collection<BinaryTagPoet> getRegionBinaryTagPoets() {
        return this.binaryTagPoetByRegion.values();
    }

    @Override
    public void setBlock(int x, int y, int z, MaterialKey materialKey) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        Chunk chunk = getChunk(chunkX, chunkZ);
        chunk.setBlock(x, y, z, materialKey);
    }

    @Override
    public void tick() {
        if (this.isPeriod(10)) {
            for (Chunk chunk : this.getChunks()) {
                long delay = System.currentTimeMillis() - chunk.getHealth().get();
                if (delay > 2000 && chunk.getViewers().isEmpty()){
                    this.flushChunk(chunk, true);
                }
            }
        }

        if (isPeriod(20)){

            for (BinaryTagPoet binaryTagPoet : this.binaryTagPoetByRegion.values()) {

                long delay = System.currentTimeMillis() - this.lastUsageRegion.get(binaryTagPoet);
                if (delay > 5000 && this.chunksByRegion.get(binaryTagPoet).isEmpty()){
                    if (binaryTagPoet.getCompoundSectionIndex().consume()) {
                        binaryTagPoet.writeCompoundSectionIndex();
                    }
                    this.lastUsageRegion.remove(binaryTagPoet);
                    this.chunksByRegion.remove(binaryTagPoet);
                    this.binaryTagPoetByRegion.remove(regionByBinaryTagPoet.remove(binaryTagPoet));

                }

            }

            worldBinaryTagMarshal.write(binaryTagDriver, this);
        }
    }

    private String regionId(Chunk chunk){
        return regionId(chunk.getX(), chunk.getZ());
    }

    private String regionId(int chunkX, int chunkZ){
        return (chunkX >> 5) +","+(chunkZ >> 5);
    }

    private String chunkId(Chunk chunk){
        return chunk.getX()+","+chunk.getZ();
    }
    private String chunkId(int chunkX, int chunkZ){
        return chunkX+","+chunkZ;
    }

    public boolean containsChunk(BinaryTagDriver binaryTagDriver, World world, int x, int z){
        BinaryTagPoet binaryTagPoet = getBinaryTagPoet(binaryTagDriver, world, x, z);
        return binaryTagPoet.getCompoundSectionIndex().containsKey(chunkId(x, z));
    }

    private BinaryTagPoet getBinaryTagPoet(BinaryTagDriver binaryTagDriver, World world, int x, int z){
        String regionId = regionId(x, z);
        return binaryTagPoetByRegion.computeIfAbsent(regionId, s -> {
            BinaryTagPoet binaryTagPoet = new BinaryTagPoet(new File(world.getIndicesFolder(), regionId + ".dat"), new File(world.getRegionFolder(), regionId + ".dat"), binaryTagDriver);
            this.regionByBinaryTagPoet.put(binaryTagPoet, s);
            this.chunksByRegion.put(binaryTagPoet, new ConcurrentLinkedQueue<>());
            this.lastUsageRegion.put(binaryTagPoet, System.currentTimeMillis());
            return binaryTagPoet;
        });
    }

    private BinaryTagPoet getBinaryTagPoet(BinaryTagDriver binaryTagDriver, World world, Chunk chunk){
        return getBinaryTagPoet(binaryTagDriver, world, chunk.getX(), chunk.getZ());
    }

}
