package eu.mshade.enderchest.world.block

import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.item.Material
import eu.mshade.enderframe.item.MaterialKey
import eu.mshade.enderframe.world.block.redstone.Redstone
import eu.mshade.enderframe.world.Vector
import eu.mshade.enderframe.world.World
import eu.mshade.enderframe.world.block.*

class RedstoneLampBlockBehavior: BlockBehavior {

    override fun place(
        player: Player,
        blockPosition: Vector,
        blockFace: BlockFace,
        cursorPosition: Vector,
        material: MaterialKey
    ): Collection<Pair<Vector, Block>> {
        val blocks = mutableListOf<Pair<Vector, Block>>()
        val block = material.toBlock()

        val metadatas = block.getMetadatas()
        metadatas.setMetadataKeyValue(TickableBlockMetadata(true))
        metadatas.setMetadataKeyValue(RedstoneStateBlockMetadata(RedstoneState.RECEIVING))

        blocks.add(Pair(blockPosition, block))
        return blocks
    }

/*    override fun updateBlock(position: Vector, world: World, block: Block, power: Int,
                             source: Vector,
                             tickableBlocks: TickableBlockRepository
    ){
        val chunk = world.getChunk(position.blockX shr 4, position.blockZ shr 4)?.join()?: return

        val neighborBlockPositions = Redstone.getNeighborBlockPositions(position)
        neighborBlockPositions.remove(source)

        val powered = power > 0

        val metadatas = block.getMetadatas()

        val lastPowered = block.hasPower()
        if (lastPowered == powered) {
            return
        }

        metadatas.setMetadataKeyValue(PowerBlockMetadata(powered))

        world.setBlock(position, block)
        chunk.notify(Player::class.java) {
            it.minecraftSession.sendBlockChange(position, block)
        }
    }*/

    override fun updateBlock(
        world: World,
        position: Vector,
        previousPosition: Vector,
        tickableBlocks: TickableBlockRepository
    ) {

        val block = world.getBlock(position)

        val metadatas = block.getMetadatas()

        val lastPowered = block.hasPower()
        val powered = world.getBlock(previousPosition).hasPower()

        if (lastPowered == powered) {
            return
        }

        metadatas.setMetadataKeyValue(PowerBlockMetadata(powered))

        world.setBlock(position, block)
        updateClientBlock(world, listOf(Pair(position, block)))
    }

    override fun canApply(material: MaterialKey): Boolean {
        return material == Material.REDSTONE_LAMP
    }
}