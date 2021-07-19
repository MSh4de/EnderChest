package eu.mshade.enderchest.world;

import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.world.WorldBuffer;
import eu.mshade.enderframe.world.WorldLevel;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class WorldManager {

    private File worldsFolder = new File(System.getProperty("user.dir"), "worlds");
    private Map<String, WorldBuffer> worlds = new ConcurrentHashMap<>();
    private WatchDogChunk watchDogChunk;
    private DedicatedEnderChest dedicatedEnderChest;
    private WorldBufferIO worldBufferIO;

    public WorldManager(DedicatedEnderChest dedicatedEnderChest) {
        this.dedicatedEnderChest = dedicatedEnderChest;
        this.worldBufferIO = new WorldBufferIO();
        this.watchDogChunk = new WatchDogChunk(dedicatedEnderChest);
        this.worldsFolder.mkdir();
        for (File file : Objects.requireNonNull(this.worldsFolder.listFiles())) {
            WorldLevel worldLevel = worldBufferIO.readWorldLevel(new File(file, "level.dat"));
            DefaultWorldBuffer worldBuffer = new DefaultWorldBuffer(this, worldLevel, file);
            worlds.put(worldLevel.getName(), worldBuffer);
        }
    }

    public WorldBuffer createWorld(WorldLevel worldLevel){
        if (!this.worlds.containsKey(worldLevel.getName())) {
            File file = new File(worldsFolder, worldLevel.getName());
            file.mkdir();
            DefaultWorldBuffer defaultWorldBuffer = new DefaultWorldBuffer(this, worldLevel, file);
            worlds.put(worldLevel.getName(), defaultWorldBuffer);
            return defaultWorldBuffer;
        }
        return getWorldBuffer(worldLevel.getName());
    }

    public Collection<WorldBuffer> getWorlds() {
        return worlds.values();
    }

    public WorldBuffer getWorldBuffer(String name){
        return this.worlds.get(name);
    }

    public DedicatedEnderChest getDedicatedEnderChest() {
        return dedicatedEnderChest;
    }

    public WatchDogChunk getWatchDogChunk() {
        return watchDogChunk;
    }

    public WorldBufferIO getWorldBufferIO() {
        return worldBufferIO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorldManager that = (WorldManager) o;
        return Objects.equals(worldsFolder, that.worldsFolder) && Objects.equals(watchDogChunk, that.watchDogChunk) && Objects.equals(worldBufferIO, that.worldBufferIO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldsFolder, watchDogChunk, worldBufferIO);
    }
}
