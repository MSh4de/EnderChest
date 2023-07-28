package eu.mshade.enderchest.world


import eu.mshade.enderchest.EnderChest
import eu.mshade.enderchest.marshal.world.ChunkBinaryTagMarshal
import eu.mshade.enderchest.marshal.world.WorldBinaryTagMarshal
import eu.mshade.enderframe.EnderFrame
import eu.mshade.enderframe.MinecraftServer
import eu.mshade.enderframe.entity.Entity
import eu.mshade.enderframe.entity.EntityKey
import eu.mshade.enderframe.event.ChunkCreateEvent
import eu.mshade.enderframe.event.ChunkLoadEvent
import eu.mshade.enderframe.event.ChunkUnloadEvent
import eu.mshade.enderframe.item.MaterialKey
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket
import eu.mshade.enderframe.world.ChunkStatus
import eu.mshade.enderframe.world.Location
import eu.mshade.enderframe.world.World
import eu.mshade.enderframe.world.block.Block
import eu.mshade.enderframe.world.chunk.Chunk
import eu.mshade.enderman.packet.play.world.MinecraftPacketOutTimeUpdate
import eu.mshade.mwork.binarytag.segment.SegmentBinaryTag
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ExecutionException


class DefaultWorld(
    private val chunkSafeguard: ChunkSafeguard,
    worldFolder: File,
    metadataKeyValueBucket: MetadataKeyValueBucket = MetadataKeyValueBucket(
        true
    ),
) : World(worldFolder, metadataKeyValueBucket) {

    private val segmentByRegion: MutableMap<String?, SegmentBinaryTag> = ConcurrentHashMap()
    private val chunksByRegion: MutableMap<SegmentBinaryTag, Queue<Chunk>> = ConcurrentHashMap()
    private val lastUsageRegion: MutableMap<SegmentBinaryTag, Long> = ConcurrentHashMap()
    private val regionByBinaryTagPoet: MutableMap<SegmentBinaryTag, String?> = ConcurrentHashMap()
    private val binaryTagDriver = EnderFrame.get().binaryTagDriver
    private val metadataKeyValueBufferRegistry = EnderChest.metadataKeyValueBufferRegistry
    private val blockBehaviorRepository = MinecraftServer.getBlockBehaviors()
    private val ticableBlocks = MinecraftServer.getTickableBlocks()





    override fun flushChunk(chunk: Chunk, save: Boolean) {
        val chunkStateStore = chunk.chunkStateStore

        chunkStateStore.setFinishWrite {
            val segmentBinaryTag = getSegment(this, chunk)
            chunksByRegion[segmentBinaryTag]!!.remove(chunk)
            chunkById.remove(chunk.id)
            ticableBlocks.flush(chunk)
            MinecraftServer.getMinecraftEvent().publish(ChunkUnloadEvent(chunk))
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
        val segmentBinaryTag = getSegment(this, chunk)
        ChunkBinaryTagMarshal.write(segmentBinaryTag, binaryTagDriver, chunk, EnderChest.metadataKeyValueBufferRegistry)
        lastUsageRegion[segmentBinaryTag] = System.currentTimeMillis()
    }

    override fun getChunk(chunkX: Int, chunkZ: Int): CompletableFuture<Chunk> {
        val id = Chunk.key(chunkX, chunkZ)
        val chunkCompletableFuture = chunkById[id]

        if (chunkCompletableFuture != null) {
            val chunk = chunkCompletableFuture.join()


/*            if (chunk != null) {
                val chunkStateStore = chunk.chunkStateStore
                if (chunkStateStore.chunkStatus != ChunkStatus.LOADED) chunkStateStore.chunkStatus = ChunkStatus.LOADED
            }*/

            return chunkCompletableFuture

        }
        val completableFuture = CompletableFuture<Chunk>()
        val segmentBinaryTag = getSegment(this, chunkX, chunkZ)
        chunkById[id] = completableFuture
        //TODO: clear chunk into @chunkById when chunk is not loaded
        if (!isChunkExists(chunkX, chunkZ)) {
            completableFuture.completeAsync {
                val chunk = DefaultChunk(chunkX, chunkZ, this)

                val chunkCreateEvent = ChunkCreateEvent(chunk)
                MinecraftServer.getMinecraftEvent().publish(chunkCreateEvent)

                if (!chunkCreateEvent.isCancelled) {

                    if (chunkGenerator != null) chunkGenerator!!.generate(chunk)
                    chunk.chunkStateStore.chunkStatus = ChunkStatus.LOADED
                    chunksByRegion.computeIfAbsent(segmentBinaryTag) { _ -> ConcurrentLinkedQueue() }
                        .add(chunk)

                    lastUsageRegion[segmentBinaryTag] = System.currentTimeMillis()
                    return@completeAsync chunk

                }
                EmptyChunk(chunkX, chunkZ, this)
            }
        } else {
            completableFuture.completeAsync {
                val chunk: Chunk
                try {
                    chunk = ChunkBinaryTagMarshal.read(
                        segmentBinaryTag,
                        binaryTagDriver,
                        this,
                        chunkX,
                        chunkZ,
                        metadataKeyValueBufferRegistry
                    )
                    val chunkLoadEvent = ChunkLoadEvent(completableFuture)
                    MinecraftServer.getMinecraftEvent().publish(chunkLoadEvent)
                    if (!chunkLoadEvent.isCancelled) {
                        chunk.chunkStateStore.chunkStatus = ChunkStatus.LOADED
                        chunksByRegion.computeIfAbsent(segmentBinaryTag) { _ -> ConcurrentLinkedQueue() }
                            .add(chunk)
                        lastUsageRegion[segmentBinaryTag] = System.currentTimeMillis()
                        return@completeAsync chunk
                    }
                } catch (e: Exception) {
                    LOGGER.error("Can't read chunk x=" + chunkX + ", z=" + chunkZ + " from region=" + regionId(chunkX, chunkZ), e)
                }
                EmptyChunk(chunkX, chunkZ, this)
            }
        }
        return completableFuture
    }

    override fun getChunk(id: Long): CompletableFuture<Chunk>? {
        return chunkById[id]!!
    }

    override fun hasChunkLoaded(chunkX: Int, chunkZ: Int): Boolean {
        return chunkById.containsKey(Chunk.key(chunkX, chunkZ))
    }

    override fun isChunkExists(chunkX: Int, chunkZ: Int): Boolean {
        val segmentBinaryTag = getSegment(this, chunkX, chunkZ)
        return segmentBinaryTag.hasKey(chunkId(chunkX, chunkZ))
    }

    override fun addEntity(entity: Entity) {
        if (entities.contains(entity)) return
        entities.add(entity)
    }

    override fun removeEntity(entity: Entity) {
        entities.remove(entity)
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
        WorldBinaryTagMarshal.write(binaryTagDriver, this, metadataKeyValueBufferRegistry)
    }

    override val regions: Collection<SegmentBinaryTag>
        get() = chunksByRegion.keys

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
                    if (chunk.watchers.isEmpty() && chunkStateStore.outdatedInteract(1000)) {
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

            for (segmentBinaryTag in segmentByRegion.values) {
                val delay = System.currentTimeMillis() - lastUsageRegion[segmentBinaryTag]!!
                if (delay > 5000 && chunksByRegion[segmentBinaryTag]!!.isEmpty()) {
                    LOGGER.info("Unload region $segmentBinaryTag, delay=$delay, chunks=${chunksByRegion[segmentBinaryTag]!!.size}")
                    lastUsageRegion.remove(segmentBinaryTag)
                    chunksByRegion.remove(segmentBinaryTag)
                    segmentByRegion.remove(regionByBinaryTagPoet.remove(segmentBinaryTag))
                }
            }

            this.saveLevel()

            for (onlinePlayer in MinecraftServer.getPlayers()) {
                onlinePlayer.minecraftSession.sendPacket(MinecraftPacketOutTimeUpdate(tick))
            }
        }

        this.chunks.forEach {
            val chunk = it.join() ?: return@forEach
            if (chunk.chunkStateStore.chunkStatus == ChunkStatus.LOADED) {
                chunk.entities.forEach { entity ->
                    entity.tick()
                }
            }
        }
    }

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
        return segmentByRegion.computeIfAbsent(regionId) { _ ->
            val segmentBinaryTag = SegmentBinaryTag(File(world.regionFolder, "$regionId.msr"))
            chunksByRegion[segmentBinaryTag] = ConcurrentLinkedQueue()
            regionByBinaryTagPoet[segmentBinaryTag] = regionId
            lastUsageRegion[segmentBinaryTag] = System.currentTimeMillis()
            segmentBinaryTag
        }
    }

    private fun getSegment(world: World, chunk: Chunk): SegmentBinaryTag {
        return getSegment(world, chunk.x, chunk.z)
    }

    override fun toString(): String {
        return "DefaultWorld{" +
                "name=" + name +
                '}'
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultWorld::class.java)
    }
}
