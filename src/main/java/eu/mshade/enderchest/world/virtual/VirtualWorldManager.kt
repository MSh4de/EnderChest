package eu.mshade.enderchest.world.virtual

import eu.mshade.enderchest.EnderChest
import eu.mshade.enderchest.marshal.world.virtual.VirtualWorldBinaryTagMarshal
import eu.mshade.enderchest.world.ChunkSafeguard
import eu.mshade.enderframe.EnderFrame
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket
import eu.mshade.enderframe.tick.TickBus
import eu.mshade.enderframe.virtualserver.VirtualWorld
import eu.mshade.enderframe.world.NameWorldMetadata
import eu.mshade.enderframe.world.ParentWorldMetadata
import eu.mshade.enderframe.world.World
import org.slf4j.LoggerFactory
import java.io.File

class VirtualWorldManager(private val chunkSafeguard: ChunkSafeguard, private val tickBus: TickBus) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(VirtualWorldManager::class.java)
    }

    private val virtualWorldsFolder = File(System.getProperty("user.dir"), "virtualWorlds")
    private val virtualWorlds = mutableMapOf<String, VirtualWorld>()
    private val binaryTagDriver = EnderFrame.get().binaryTagDriver


    init {
        virtualWorldsFolder.mkdirs()
        for (file in this.virtualWorldsFolder.listFiles()!!) {
            val world = VirtualWorldBinaryTagMarshal.read(binaryTagDriver, chunkSafeguard, file, EnderChest.metadataKeyValueBufferRegistry)
            virtualWorlds[world.name] = world
            world.joinTickBus(tickBus)
        }
    }

    fun createVirtualWorld(name: String, parent: World): World{

        if (virtualWorlds.containsKey(name)) {
            throw IllegalArgumentException("World with name $name already exists")
        }

        val file = File(virtualWorldsFolder, name)
        file.mkdir()
        val metadataKeyValueBucket = MetadataKeyValueBucket()
        metadataKeyValueBucket.setMetadataKeyValue(NameWorldMetadata(name))
        metadataKeyValueBucket.setMetadataKeyValue(ParentWorldMetadata(parent))
        val world = DefaultVirtualWorld(chunkSafeguard, file, metadataKeyValueBucket)
        virtualWorlds[name] = world
        world.joinTickBus(tickBus)
        return world
    }

    fun getVirtualWorld(name: String): VirtualWorld? {
        return virtualWorlds[name]
    }

    fun getVirtualWorldsFrom(world: World): Collection<VirtualWorld> {
        return virtualWorlds.values.filter { it.getParentWorld() == world }
    }

    fun getVirtualWorlds(): Collection<VirtualWorld> {
        return virtualWorlds.values
    }

}