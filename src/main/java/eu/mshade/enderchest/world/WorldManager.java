package eu.mshade.enderchest.world;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderchest.marshal.world.WorldBinaryTagMarshal;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.enderframe.tick.TickBus;
import eu.mshade.enderframe.world.NameWorldMetadata;
import eu.mshade.enderframe.world.World;
import eu.mshade.enderframe.world.WorldRepository;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;
import java.util.function.Consumer;

public class WorldManager {

    private static Logger LOGGER = LoggerFactory.getLogger(WorldManager.class);
    private File worldsFolder = new File(System.getProperty("user.dir"), "worlds");
    private final ChunkSafeguard chunkSafeguard;
    private final TickBus tickBus;

    public WorldManager(BinaryTagDriver binaryTagDriver, ChunkSafeguard chunkSafeguard, TickBus tickBus) {
        this.chunkSafeguard = chunkSafeguard;
        this.tickBus = tickBus;
        this.worldsFolder.mkdir();

        for (File file : Objects.requireNonNull(this.worldsFolder.listFiles())) {
            World world = WorldBinaryTagMarshal.INSTANCE.read(binaryTagDriver, file, chunkSafeguard, EnderChest.INSTANCE.getMetadataKeyValueBufferRegistry());
            world.joinTickBus(tickBus);
            WorldRepository.INSTANCE.addWorld(world);
        }

    }

    public World createWorld(String name, Consumer<MetadataKeyValueBucket> bucketConsumer) {
        World world = WorldRepository.INSTANCE.getWorld(name);
        if (world != null) return world;
        File file = new File(worldsFolder, name);
        file.mkdir();
        world = new DefaultWorld(chunkSafeguard, file);
        MetadataKeyValueBucket metadataKeyValueBucket = world.getMetadataKeyValueBucket();
        metadataKeyValueBucket.setMetadataKeyValue(new NameWorldMetadata(name));
        bucketConsumer.accept(metadataKeyValueBucket);
        world.joinTickBus(tickBus);
        WorldRepository.INSTANCE.addWorld(world);
        return world;
    }

    public ChunkSafeguard getChunkSafeguard() {
        return chunkSafeguard;
    }
}
