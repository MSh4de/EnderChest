package eu.mshade.enderchest.world.virtual

import eu.mshade.enderframe.Agent
import eu.mshade.enderframe.virtualserver.VirtualWorld
import eu.mshade.enderframe.world.ChunkStatus
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

        section = getSectionOrCreate(y)
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


        if (targetBlock != null) {
            val blockId = palette.getId(targetBlock)
            if (blockId != null) {
                palette.removeCount(blockId)
                val blockCount = palette.getCount(blockId)
                if (blockCount <= 0) palette.deleteBlock(blockId)
            }
        }

        var blockId = palette.getId(block)
        if (blockId == null) {
            blockId = section.uniqueId.freeId
            palette.setBlock(blockId, block)
        }
        palette.addCount(blockId!!)
        section.blocks[index] = blockId


        getChunkStateStore().interact()

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

    override fun getSectionOrCreate(y: Int): Section {
        val realY = y shr 4
        if (sections[realY] == null) {
            sections[realY] = createSection(y)
        }
        return sections[realY]
    }

    override fun createSection(y: Int): Section {
        val realY = y shr 4

        if (sections[realY] != null) {
            throw IllegalStateException("Section already exists")
        }

        sections[realY] = VirtualSection(this, realY)
        return sections[realY]
    }

    override fun getBiomes(): ByteArray {
        return ByteArray(256)
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

    override fun getWatching(): Collection<Agent?> {
        return agents
    }

    override fun isWatching(agent: Agent?): Boolean {
        return agents.contains(agent)
    }

    override fun notify(agentConsumer: Consumer<Agent>) {
        for (agent in agents) {
            agentConsumer.accept(agent)
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
