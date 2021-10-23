package eu.mshade.enderchest.world;

import eu.mshade.enderchest.entity.DefaultPlayerEntity;
import eu.mshade.enderchest.entity.EntityFactory;
import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.EnderFrameSession;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.GameMode;
import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.EntityIdManager;
import eu.mshade.enderframe.entity.EntityType;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.event.ChunkLoadEvent;
import eu.mshade.enderframe.event.ChunkUnloadEvent;
import eu.mshade.enderframe.world.*;
import eu.mshade.mwork.ParameterContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultWorldBuffer implements WorldBuffer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWorldBuffer.class);

    private final WorldLevel worldLevel;
    private final File chunksFolder;
    private final File worldFolder;
    private ChunkGenerator chunkGenerator;
    private final Map<UUID, ChunkBuffer> chunks = new ConcurrentHashMap<>();
    private final Map<UUID, File> chunkFiles = new ConcurrentHashMap<>();
    private final WorldManager worldManager;
    private final Queue<Entity> entities = new ConcurrentLinkedQueue<>();

    public DefaultWorldBuffer(WorldManager worldManager, WorldLevel worldLevel, File worldFolder) {
        this.worldManager = worldManager;
        this.worldLevel = worldLevel;
        this.worldFolder = worldFolder;
        this.chunksFolder =  new File(worldFolder,"chunks");
        chunksFolder.mkdirs();
        LOGGER.info(String.format("Indexing of chunks in the world (%s)", worldLevel.getName()));
        /*
        for (File file : Objects.requireNonNull(chunksFolder.listFiles())) {
            String[] strings = file.getName().split(",");
            int x = Integer.parseInt(strings[0]);
            int z = Integer.parseInt(strings[1].split("\\.")[0]);
            chunkFiles.put(ChunkBuffer.ofId(x, z), file);
        }
        LOGGER.info(String.format("Indexing done with %d chunks", chunkFiles.size()));

         */
    }


    @Override
    public WorldLevel getWorldLevel() {
        return worldLevel;
    }

    @Override
    public Collection<ChunkBuffer> getChunkBuffers() {
        return chunks.values();
    }

    @Override
    public void flushChunkBuffer(ChunkBuffer chunkBuffer) {
        //this.chunkFiles.computeIfAbsent(chunkBuffer.getId(), integer -> chunkBuffer.getFile());
        ChunkUnloadEvent chunkUnloadEvent = new ChunkUnloadEvent(chunkBuffer);
        EnderFrame.get().getEnderFrameEventBus().publish(chunkUnloadEvent);

        if(!chunkUnloadEvent.isCancelled()) {
            worldManager.getWorldBufferIO().writeChunkBuffer(chunkBuffer);
            chunkBuffer.clearEntities();
            this.chunks.remove(chunkBuffer.getId());
        }
    }

    @Override
    public ChunkBuffer getChunkBuffer(int x, int z) {

        final UUID id = ChunkBuffer.ofId(x, z);
        ChunkBuffer chunkBuffer = chunks.get(id);
        if (chunkBuffer != null){
            ChunkLoadEvent chunkLoadEvent = new ChunkLoadEvent(chunkBuffer);
            EnderFrame.get().getEnderFrameEventBus().publish(chunkLoadEvent);
            if(!chunkLoadEvent.isCancelled())
                return chunkBuffer;
        }else {
            File file = getChunkFile(x, z);
            WatchDogChunk watchDogChunk = this.worldManager.getWatchDogChunk();

            if (!file.exists() && !chunks.containsKey(id)) {
                file = new File(chunksFolder, String.format("%d,%d.dat", x, z));
                DefaultChunkBuffer buffer = new DefaultChunkBuffer(x, z, true, this, file);
                ChunkLoadEvent chunkLoadEvent = new ChunkLoadEvent(buffer);
                EnderFrame.get().getEnderFrameEventBus().publish(chunkLoadEvent);
                if (!chunkLoadEvent.isCancelled()) {
                    getChunkGenerator().generate(buffer);
                    watchDogChunk.addChunkBuffer(buffer);
                    chunks.put(id, buffer);
                    return buffer;
                }
            }else {
                ChunkBuffer readChunkBuffer = worldManager.getWorldBufferIO().readChunkBuffer(this, worldManager, file);
                ChunkLoadEvent chunkLoadEvent = new ChunkLoadEvent(readChunkBuffer);
                EnderFrame.get().getEnderFrameEventBus().publish(chunkLoadEvent);

                if (!chunkLoadEvent.isCancelled()) {
                    watchDogChunk.addChunkBuffer(readChunkBuffer);
                    chunks.put(id, readChunkBuffer);

                    return readChunkBuffer;
                }
            }
        }
        return null;
    }


    @Override
    public ChunkBuffer getChunkBuffer(UUID id) {
        return chunks.get(id);
    }

    @Override
    public void addChunkBuffer(ChunkBuffer chunkBuffer) {
        this.worldManager.getWatchDogChunk().addChunkBuffer(chunkBuffer);
        this.chunks.put(chunkBuffer.getId(), chunkBuffer);
    }


    @Override
    public boolean hasFileChunkBuffer(int x, int z) {
        return chunkFiles.containsKey(ChunkBuffer.ofId(x, z));
    }

    @Override
    public File getChunkFile(int chunkX, int chunkZ) {
        return new File(getChunksFolder(), chunkX+","+chunkZ+".dat");
        //return this.getChunkFile(ChunkBuffer.ofId(chunkX, chunkZ));
    }

    @Override
    public File getChunkFile(UUID id) {
        return this.chunkFiles.get(id);
    }

    @Override
    public boolean hasChunkBuffer(int x, int z) {
        return chunks.containsKey(ChunkBuffer.ofId(x, z));
    }

    @Override
    public ChunkGenerator getChunkGenerator() {
        return chunkGenerator;
    }


    @Override
    public void setChunkGenerator(ChunkGenerator chunkGenerator) {
        this.chunkGenerator = chunkGenerator;
    }

    @Override
    public File getChunksFolder() {
        return chunksFolder;
    }

    @Override
    public File getWorldFolder() {
        return worldFolder;
    }

    @Override
    public Queue<Entity> getEntities() {
       return this.entities;
    }
    @Override
    public void addEntity(Entity entity) {
        if(this.entities.contains(entity)) return;
        this.entities.add(entity);
    }

    @Override
    public void removeEntity(Entity entity) {
        this.entities.remove(entity);
    }

    @Override
    public Entity spawnEntity(EntityType entityType, Location location) {
        if(location == null)
            throw new NullPointerException("Location cannot be null when trying to spawn an entity.");

        EntityFactory entityFactory = EntityFactory.get();
        EntityIdManager entityIdManager = EntityIdManager.get();
        Location entityLocation = location.clone();
        
        try {
            int id = entityIdManager.getFreeId();
            Entity entity = entityFactory.factoryEntity(entityType, ParameterContainer.of()
                    .putContainer(id)
                    .putContainer(entityLocation));

            location.getChunkBuffer().addEntity(entity);
            location.getChunkBuffer().getViewers().stream()
                    .filter(player -> player.getLocation().distance(entityLocation) <= 64)
                    .map(Player::getEnderFrameSessionHandler)
                    .map(EnderFrameSessionHandler::getEnderFrameSession)
                    .forEach(session -> session.sendEntity(entity));

            return entity;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Player spawnPlayer(EnderFrameSessionHandler sessionHandler, Location location) {
        if(location == null)
            throw new NullPointerException("Location cannot be null when trying to spawn an entity.");

        try {
            EnderFrameSession enderFrameSession = sessionHandler.getEnderFrameSession();
            Player player = new DefaultPlayerEntity(location.clone(), enderFrameSession.getEntityId(), sessionHandler, GameMode.SURVIVAL, enderFrameSession.getGameProfile());
            enderFrameSession.sendEntity(player);
            return player;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultWorldBuffer that = (DefaultWorldBuffer) o;
        return Objects.equals(worldLevel, that.worldLevel) && Objects.equals(chunksFolder, that.chunksFolder) && Objects.equals(worldFolder, that.worldFolder) && Objects.equals(worldManager, that.worldManager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldLevel, chunksFolder, worldFolder, worldManager);
    }


}
