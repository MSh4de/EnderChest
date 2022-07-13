package eu.mshade.enderchest.world;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderchest.marshal.world.WorldBinaryTagMarshal;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.enderframe.metadata.world.WorldMetadataType;
import eu.mshade.enderframe.world.World;
import eu.mshade.enderframe.world.metadata.NameWorldMetadata;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class WorldManager {

    private static Logger logger = LoggerFactory.getLogger(WorldManager.class);
    private File worldsFolder = new File(System.getProperty("user.dir"), "worlds");
    private Map<String, World> worlds = new ConcurrentHashMap<>();
    private EnderChest enderChest;

    public WorldManager(BinaryTagDriver binaryTagDriver, EnderChest enderChest) {
        this.enderChest = enderChest;
        this.worldsFolder.mkdir();

        WorldBinaryTagMarshal worldBinaryTagMarshal = binaryTagDriver.getDynamicMarshal(WorldBinaryTagMarshal.class);

        for (File file : Objects.requireNonNull(this.worldsFolder.listFiles())) {
            World world = worldBinaryTagMarshal.read(binaryTagDriver, file, this);
            world.joinTickBus(enderChest.getTickBus());
            worlds.put(world.getName(), world);
        }

    }

    public World createWorld(String name, Consumer<MetadataKeyValueBucket> bucketConsumer){
        if (!this.worlds.containsKey(name)) {
            File file = new File(worldsFolder, name);
            file.mkdir();
            DefaultWorld world = new DefaultWorld(this, file);
            MetadataKeyValueBucket metadataKeyValueBucket = world.getMetadataKeyValueBucket();
            metadataKeyValueBucket.setMetadataKeyValue(new NameWorldMetadata(name));
            bucketConsumer.accept(metadataKeyValueBucket);
            world.joinTickBus(enderChest.getTickBus());
            worlds.put(name, world);
            return world;
        }
        return getWorld(name);
    }

    public Collection<World> getWorlds() {
        return worlds.values();
    }

    public World getWorld(String name){
        return this.worlds.get(name);
    }

}
