package eu.mshade.enderchest.world.virtual

import eu.mshade.enderchest.EnderChest
import eu.mshade.enderchest.marshal.world.virtual.VirtualChunkBinaryTagMarshal
import eu.mshade.enderchest.marshal.world.WorldBinaryTagMarshal.write
import eu.mshade.enderchest.world.ChunkSafeguard
import eu.mshade.enderchest.world.EmptyChunk
import eu.mshade.enderframe.EnderFrame
import eu.mshade.enderframe.MinecraftServer
import eu.mshade.enderframe.entity.Entity
import eu.mshade.enderframe.entity.EntityKey
import eu.mshade.enderframe.item.MaterialKey
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket
import eu.mshade.enderframe.virtualserver.VirtualWorld
import eu.mshade.enderframe.world.ChunkStatus
import eu.mshade.enderframe.world.Dimension
import eu.mshade.enderframe.world.Location
import eu.mshade.enderframe.world.World
import eu.mshade.enderframe.world.block.Block
import eu.mshade.enderframe.world.chunk.Chunk
import eu.mshade.mwork.binarytag.segment.SegmentBinaryTag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ExecutionException

class DefaultVirtualWorld(
    private val chunkSafeguard: ChunkSafeguard,
    worldDirectory: File,
    metadataKeyValueBucket: MetadataKeyValueBucket,
) : VirtualWorld(worldDirectory, metadataKeyValueBucket) {

    private val regionById = ConcurrentHashMap<String, SegmentBinaryTag>()
    private val chunksByRegion = ConcurrentHashMap<SegmentBinaryTag, Queue<Chunk>>()
    private val lastUsageRegion = ConcurrentHashMap<SegmentBinaryTag, Long>()
    private val regionByBinaryTagPoet = ConcurrentHashMap<SegmentBinaryTag, String>()
    private val binaryTagDriver = EnderFrame.get().binaryTagDriver

    private val blockBehaviorRepository = MinecraftServer.getBlockBehaviors()


    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(DefaultVirtualWorld::class.java)
    }

    override fun flushChunk(chunk: Chunk, save: Boolean) {
        val chunkStateStore = chunk.chunkStateStore

        chunkStateStore.setFinishWrite {
            val segmentBinaryTag = getSegment(this, chunk)
            chunksByRegion[segmentBinaryTag]!!.remove(chunk)
            chunkById.remove(chunk.id)
        }

        if (save && !chunkStateStore.isInChunkSafeguard) {
            chunkSafeguard.addChunk(chunk)
            return
        }

        if (!save) {
            chunkStateStore.finishWrite()
        }
    }

    override fun saveChunk(chunk: Chunk) {
        val carbonBinaryTag = getSegment(this, chunk)
        VirtualChunkBinaryTagMarshal.write(
            carbonBinaryTag,
            binaryTagDriver,
            chunk as VirtualChunk,
            EnderChest.metadataKeyValueBufferRegistry
        )
        lastUsageRegion[carbonBinaryTag] = System.currentTimeMillis()
    }

    override fun getChunk(chunkX: Int, chunkZ: Int): CompletableFuture<Chunk> {
        val id = Chunk.key(chunkX, chunkZ)

        var chunkCompletableFuture = chunkById[id]

        if (chunkCompletableFuture != null) {
            val chunk = chunkCompletableFuture.join()
            if (chunk != null) {
                val chunkStateStore = chunk.chunkStateStore
                if (chunkStateStore.chunkStatus != ChunkStatus.LOADED) chunkStateStore.chunkStatus = ChunkStatus.LOADED
            }
            return chunkCompletableFuture
        }

        val carbonBinaryTag = getSegment(this, chunkX, chunkZ)
        if (isChunkExists(chunkX, chunkZ)) {
            chunkCompletableFuture = CompletableFuture()
            chunkCompletableFuture.completeAsync {
                val chunk: Chunk
                try {
                    chunk = VirtualChunkBinaryTagMarshal.read(
                        carbonBinaryTag,
                        binaryTagDriver,
                        this,
                        chunkX,
                        chunkZ,
                        EnderChest.metadataKeyValueBufferRegistry
                    )

                    chunk.chunkStateStore.chunkStatus = ChunkStatus.LOADED
                    chunkById[id] = chunkCompletableFuture!!
                    chunksByRegion.computeIfAbsent(carbonBinaryTag) { ConcurrentLinkedQueue() }.add(chunk)
                    lastUsageRegion[carbonBinaryTag] = System.currentTimeMillis()
                    return@completeAsync chunk
                } catch (e: Throwable ) {
                    LOGGER.error("Can't read chunk x=$chunkX, z=$chunkZ from region=" + regionId(chunkX, chunkZ), e)
                }
                return@completeAsync EmptyChunk(chunkX, chunkZ, this)
            }
            return chunkCompletableFuture
        }
            try {
                val parentChunk = getParentWorld().getChunk(chunkX, chunkZ)!!.get()
                val chunk = toVirtualChunk(parentChunk)
                chunkCompletableFuture = CompletableFuture.completedFuture(chunk)
                chunk.chunkStateStore.chunkStatus = ChunkStatus.LOADED
                chunkById[id] = chunkCompletableFuture
                chunksByRegion.computeIfAbsent(carbonBinaryTag) { ConcurrentLinkedQueue() }.add(chunk)
                lastUsageRegion[carbonBinaryTag] = System.currentTimeMillis()
                return chunkCompletableFuture
            } catch (e: Throwable ) {
                LOGGER.error("Can't get parent chunk x=$chunkX, z=$chunkZ", e)
            }

        return CompletableFuture.completedFuture(EmptyChunk(chunkX, chunkZ, this))

    }

    override fun getChunk(id: Long): CompletableFuture<Chunk> {
        return chunkById[id]!!
    }

    override fun hasChunkLoaded(x: Int, z: Int): Boolean {
        return chunkById.containsKey(Chunk.key(x, z))
    }

    override fun isChunkExists(chunkX: Int, chunkZ: Int): Boolean {
        val carbonBinaryTag = getSegment(this, chunkX, chunkZ)
        return carbonBinaryTag.hasKey(chunkId(chunkX, chunkZ))
    }

    override fun addEntity(entity: Entity) {
        TODO("Not yet implemented")
    }

    override fun removeEntity(entity: Entity) {
        TODO("Not yet implemented")
    }

    override fun spawnEntity(entityType: EntityKey, location: Location): Entity {
        TODO("Not yet implemented")
    }


    override fun setBlock(x: Int, y: Int, z: Int, materialKey: MaterialKey) {
        val chunkX = x shr 4
        val chunkZ = z shr 4

        try {
            val chunk = getChunk(chunkX, chunkZ).get()
            chunk.setBlock(x, y, z, materialKey.toBlock())
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        } catch (e: ExecutionException) {
            throw RuntimeException(e)
        }
    }

    override fun setBlock(x: Int, y: Int, z: Int, block: Block) {
        val chunkX = x shr 4
        val chunkZ = z shr 4

        try {
            val chunk = getChunk(chunkX, chunkZ).get()
            chunk.setBlock(x, y, z, block)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        } catch (e: ExecutionException) {
            throw RuntimeException(e)
        }
    }

    override fun getBlock(x: Int, y: Int, z: Int): Block {
        val chunkX = x shr 4
        val chunkZ = z shr 4

        return try {
            val chunk = getChunk(chunkX, chunkZ).get()
            chunk.getBlock(x, y, z)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        } catch (e: ExecutionException) {
            throw RuntimeException(e)
        }
    }

    override fun saveLevel() {
        write(binaryTagDriver, this, EnderChest.metadataKeyValueBufferRegistry)
    }

    override fun tick() {
        if (isPeriod(20)) {
            for (chunkCompletableFuture in this.chunks) {
                val chunk = chunkCompletableFuture.join() ?: continue
                val chunkStateStore = chunk.chunkStateStore
                if (chunkStateStore.chunkStatus == ChunkStatus.LOADED) {
                    if (chunkStateStore.addAndGetAge(1) % 30 == 0 && !chunkStateStore.isInChunkSafeguard && chunkStateStore.isAutoSaveEnabled) {
                        chunkSafeguard.addChunk(chunk)
                        chunkStateStore.resetAge()
                    }
                    if (chunk.watchers.isEmpty() && chunkStateStore.outdatedInteract(500)) {
                        chunkStateStore.chunkStatus = ChunkStatus.PREPARE_TO_UNLOAD
                    }
                }
                if (chunkStateStore.chunkStatus == ChunkStatus.PREPARE_TO_UNLOAD && chunkStateStore.outdatedChunkStatus(
                        2000
                    ) || chunkStateStore.chunkStatus == ChunkStatus.PREPARE_TO_LOAD && chunkStateStore.outdatedChunkStatus(
                        ChunkStatus.PREPARE_TO_LOAD,
                        10000
                    )
                ) {
                    this.flushChunk(chunk)
                }
            }
            for (carbonBinaryTag in regionById.values) {
                val delay = System.currentTimeMillis() - lastUsageRegion[carbonBinaryTag]!!
                if (delay > 5000 && chunksByRegion[carbonBinaryTag]!!.isEmpty()) {
                    lastUsageRegion.remove(carbonBinaryTag)
                    chunksByRegion.remove(carbonBinaryTag)
                    regionById.remove(regionByBinaryTagPoet.remove(carbonBinaryTag))
                }
            }
            saveLevel()
        }
    }


    override val dimension: Dimension?
        get() = getParentWorld().dimension

    override val regions: Collection<SegmentBinaryTag>
        get() = regionById.values

    /*override fun getDimension(): Dimension {
        return this.getParentWorld().dimension
    }
*/
    private fun regionId(chunk: Chunk): String {
        return regionId(chunk.x, chunk.z)
    }

    private fun regionId(chunkX: Int, chunkZ: Int): String {
        return (chunkX shr 5).toString() + "," + (chunkZ shr 5)
    }

    private fun chunkId(chunk: Chunk): String {
        return chunk.x.toString() + "," + chunk.z
    }

    private fun chunkId(chunkX: Int, chunkZ: Int): String {
        return "$chunkX,$chunkZ"
    }


    private fun getSegment(world: World, x: Int, z: Int): SegmentBinaryTag {
        val regionId = regionId(x, z)
        return regionById.computeIfAbsent(regionId) { s: String ->
            val segmentBinaryTag = SegmentBinaryTag(
                File(world.regionFolder, "$regionId.msr"),
            )
            chunksByRegion[segmentBinaryTag] = ConcurrentLinkedQueue()
            regionByBinaryTagPoet[segmentBinaryTag] = s
            lastUsageRegion[segmentBinaryTag] = System.currentTimeMillis()
            return@computeIfAbsent segmentBinaryTag
        }
    }

    private fun getSegment(world: World, chunk: Chunk): SegmentBinaryTag {
        return getSegment(world, chunk.x, chunk.z)
    }

    fun toVirtualChunk(chunk: Chunk): VirtualChunk {
        val virtualChunk = VirtualChunk(chunk.x, chunk.z, this)
        for(i in 0 .. 15){
            val section = chunk.sections[i]
            if(section != null){
                virtualChunk.sections[i] = virtualChunk.copySection(section)
            }
        }
        return virtualChunk

    }


}
