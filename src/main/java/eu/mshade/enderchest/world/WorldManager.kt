package eu.mshade.enderchest.world

import eu.mshade.enderchest.EnderChest
import eu.mshade.enderchest.marshal.world.WorldBinaryTagMarshal
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket
import eu.mshade.enderframe.tick.TickBus
import eu.mshade.enderframe.world.NameWorldMetadata
import eu.mshade.enderframe.world.World
import eu.mshade.enderframe.world.WorldRepository
import eu.mshade.mwork.binarytag.BinaryTagDriver
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.attribute.FileAttribute
import java.util.*
import java.util.function.Consumer

class WorldManager(
    binaryTagDriver: BinaryTagDriver,
    private val chunkSafeguard: ChunkSafeguard,
    private val tickBus: TickBus
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorldManager::class.java)
    }

    private val worldsFolder = File(System.getProperty("user.dir"), "worlds")

    init {
        try {
            //create directory if not exists
            Files.createDirectories(worldsFolder.toPath())
        } catch (e: IOException) {
            LOGGER.error("Error creating worlds directory", e)
        }
        loadWorlds(binaryTagDriver)
    }

    fun createWorld(name: String, bucketConsumer: Consumer<MetadataKeyValueBucket>): World {
        var world = EnderChest.worldRepository.getWorld(name)
        if (world != null) {
            return world
        }
        val file = File(worldsFolder, name)

        try {
            Files.createDirectories(file.toPath())
        } catch (e: IOException) {
            LOGGER.error("Error creating world directory", e)
        }

        world = DefaultWorld(chunkSafeguard, file)
        val metadataKeyValueBucket = world.metadatas
        metadataKeyValueBucket.setMetadataKeyValue(NameWorldMetadata(name))
        bucketConsumer.accept(metadataKeyValueBucket)
        world.joinTickBus(tickBus)
        EnderChest.worldRepository.addWorld(world)

        return world
    }

    private fun loadWorlds(binaryTagDriver: BinaryTagDriver) {
        val worldFolders = Objects.requireNonNull(worldsFolder.listFiles())
        for (worldFolder in worldFolders) {
            loadWorld(binaryTagDriver, worldFolder)
        }
    }

    private fun loadWorld(binaryTagDriver: BinaryTagDriver, worldFolder: File) {
        val world = WorldBinaryTagMarshal.read(binaryTagDriver, worldFolder, chunkSafeguard, EnderChest.metadataKeyValueBufferRegistry)
        world.joinTickBus(tickBus)
        EnderChest.worldRepository.addWorld(world)
    }
}
