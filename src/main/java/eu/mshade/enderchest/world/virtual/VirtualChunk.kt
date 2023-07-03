package eu.mshade.enderchest.world.virtual

import eu.mshade.enderframe.Agent
import eu.mshade.enderframe.virtualserver.VirtualWorld
import eu.mshade.enderframe.world.ChunkStatus
import eu.mshade.enderframe.world.Vector
import eu.mshade.enderframe.world.block.Block
import eu.mshade.enderframe.world.chunk.Chunk
import eu.mshade.enderframe.world.chunk.Section
import java.util.function.Consumer

class VirtualChunk(x: Int, z: Int, private val virtualWorld: VirtualWorld) : Chunk(x, z, virtualWorld) {

    private val agents = mutableListOf<Agent>()

    override fun getBlock(x: Int, y: Int, z: Int): Block {
        val realY = y shr 4
        var section = getSection(realY)

        if (section == null) {
            val parentWorld = virtualWorld.getParentWorld()
            return parentWorld.getBlock(x, y, z)
        }

        return section.getBlock(getBlockIndex(x, y, z))
    }


    override fun setBlock(x: Int, y: Int, z: Int, block: Block) {
        if (y < 0 || y > 255) return
        val realY = y shr 4


        val section = getSectionOrCreate(realY)

        val palette = section.palette
        val index = getBlockIndex(x, y, z)
        val targetBlock = section.getBlock(index)

        if (block == targetBlock) return

        (section as VirtualSection).virtualSectionStatus = VirtualSectionStatus.DIFFERENT


        checkNotNull(targetBlock) { "Block at $x, $y, $z is null" }

        val targetPalette = palette.getBlockEntry(targetBlock)
            ?: throw IllegalStateException("PaletteEntry at " + x + ", " + y + ", " + z + " is null for block " + targetBlock.getMaterial().namespacedKey)



        targetPalette.count = targetPalette.count - 1
        if (targetPalette.count == 0) {
            palette.deleteBlock(targetBlock)
            section.getUniqueId().flushId(targetPalette.id)
        }

        val blockPalette = palette.getBlockEntry(block)
        if (blockPalette == null) {
            palette.setBlock(section.getUniqueId().freeId, block, 1)
        } else {
            blockPalette.count = blockPalette.count + 1
        }


        chunkStateStore.interact()

    }

    override fun getBlockLight(x: Int, y: Int, z: Int): Byte {
        TODO("Not yet implemented")
    }

    override fun setBlockLight(x: Int, y: Int, z: Int, light: Byte) {
        TODO("Not yet implemented")
    }

    override fun getSkyLight(x: Int, y: Int, z: Int): Byte {
        TODO("Not yet implemented")
    }

    override fun setSkyLight(x: Int, y: Int, z: Int, light: Byte) {
        TODO("Not yet implemented")
    }

    override fun setBiome(x: Int, z: Int, biome: Int) {
        TODO("Not yet implemented")
    }

    override fun getBiome(x: Int, z: Int): Int {
        TODO("Not yet implemented")
    }

    override var biomes: ByteArray
        get() = ByteArray(0)
        set(value) {
            throw UnsupportedOperationException()
        }

    override fun getSectionOrCreate(y: Int): Section {
        if (sections[y] == null) {
            sections[y] = createSection(y)
        }
        return sections[y]!!
    }

    override fun createSection(y: Int): Section {

        if (sections[y] != null) {
            throw IllegalStateException("Section already exists")
        }

        sections[y] = VirtualSection(this, y, VirtualSectionStatus.DIFFERENT)
        return sections[y]!!
    }


    override fun addWatcher(agent: Agent) {
        agents.add(agent)
        if (chunkStateStore.chunkStatus != ChunkStatus.LOADED) {
            chunkStateStore.chunkStatus = ChunkStatus.LOADED
        }
    }

    override fun removeWatcher(agent: Agent) {
        agents.remove(agent)
    }

    override fun isWatching(agent: Agent?): Boolean {
        return agents.contains(agent)
    }

    override fun getWatchers(): MutableCollection<Agent> {
        return agents
    }

    override fun notify(consumer: Consumer<Agent>) {
        for (agent in agents) {
            consumer.accept(agent)
        }
    }

    override fun toString(): String {
        return "VirtualChunk(x=$x, z=$z, virtualWorld=$virtualWorld)"
    }


    fun copySection(src: Section): Section {
        return VirtualSection(
            this,
            src.y,
            VirtualSectionStatus.SAME,
            src.palette.clone(),
            src.blocks.clone(),
            src.uniqueId.clone(),
            src.blockLight.snapshot(),
            src.skyLight.snapshot()
        )
    }


}
