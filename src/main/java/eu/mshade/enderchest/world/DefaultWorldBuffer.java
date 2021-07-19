package eu.mshade.enderchest.world;

import eu.mshade.enderframe.world.ChunkBuffer;
import eu.mshade.enderframe.world.ChunkGenerator;
import eu.mshade.enderframe.world.WorldBuffer;
import eu.mshade.enderframe.world.WorldLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class DefaultWorldBuffer implements WorldBuffer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWorldBuffer.class);

    private WorldLevel worldLevel;
    private File chunksFolder;
    private File worldFolder;
    private ChunkGenerator chunkGenerator;
    private final Map<UUID, ChunkBuffer> chunks = new ConcurrentHashMap<>();
    private final Map<UUID, File> chunkFiles = new ConcurrentHashMap<>();
    private final WorldManager worldManager;

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
        worldManager.getWorldBufferIO().writeChunkBuffer(chunkBuffer);
        this.chunks.remove(chunkBuffer.getId());
    }

    @Override
    public ChunkBuffer getChunkBuffer(int x, int z) {
        final UUID id = ChunkBuffer.ofId(x, z);
        ChunkBuffer chunkBuffer = chunks.get(id);
        if (chunkBuffer != null) return chunkBuffer;

        File file = getChunkFile(x, z);
        WatchDogChunk watchDogChunk = this.worldManager.getWatchDogChunk();

        if (!file.exists() && !chunks.containsKey(id)) {
            file = new File(chunksFolder, String.format("%d,%d.dat", x, z));
            DefaultChunkBuffer buffer = new DefaultChunkBuffer(x, z, true, this, file);
            getChunkGenerator().generate(buffer);
            watchDogChunk.addChunkBuffer(buffer);
            chunks.put(id, buffer);
            return buffer;
        }
        ChunkBuffer readChunkBuffer = worldManager.getWorldBufferIO().readChunkBuffer(this, file);
        watchDogChunk.addChunkBuffer(readChunkBuffer);
        chunks.put(id, readChunkBuffer);
        return readChunkBuffer;
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
