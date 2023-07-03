package eu.mshade.enderchest.world.block

import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.item.Material
import eu.mshade.enderframe.item.MaterialKey
import eu.mshade.enderframe.world.block.redstone.Redstone
import eu.mshade.enderframe.world.Vector
import eu.mshade.enderframe.world.World
import eu.mshade.enderframe.world.block.*
import kotlin.math.max

class RedstoneWireBlockBehavior : BlockBehavior {

    override fun place(
        player: Player,
        blockPosition: Vector,
        blockFace: BlockFace,
        cursorPosition: Vector,
        material: MaterialKey
    ): Collection<Pair<Vector, Block>> {
        val blocks = mutableListOf<Pair<Vector, Block>>()

        val block = material.toBlock()
        val metadataKeyValueBucket = block.getMetadatas()
        metadataKeyValueBucket.setMetadataKeyValue(TickableBlockMetadata(true))
        metadataKeyValueBucket.setMetadataKeyValue(RedstoneStateBlockMetadata(RedstoneState.WIRE))

        blocks.add(Pair(blockPosition, block))

        return blocks
    }

/*    override fun updateBlock(world: World, position: Vector, previousPosition: Vector, tickableBlocks: TickableBlockRepository) {
        val spreadPower = max(power - 1, 0)
        val neighborBlockPositions = Redstone.getNeighborBlockPositions(position)
        neighborBlockPositions.remove(source)
        val filterSpreadSignal = Redstone.filterSpreadSignal(position, world, neighborBlockPositions)
        val metadatas = block.getMetadatas()
        val currentPower = block.getPower()

        if (currentPower == 15 && power == 15 && source === Vector.ZERO) {
            val (higherLocation, higherPower) = Redstone.getHigherPower(world, neighborBlockPositions, position)

            println("higherLocation: $higherLocation, higherPower: $higherPower at $position")

            for (neighborBlockPosition in filterSpreadSignal) {
                val neighborBlock = world.getBlock(neighborBlockPosition)
                val neighborPower = neighborBlock.getPower()

                println("Trying to spread to $neighborBlockPosition with power $neighborPower")

                if (neighborPower == spreadPower || (neighborPower > power && power != 0)) continue

                println("spread to $neighborBlockPosition")
                tickableBlocks.blockBehaviorRepository.getBlockBehavior(neighborBlock.getMaterial())
                    ?.updateBlock(neighborBlockPosition, world, neighborBlock, spreadPower, position, tickableBlocks)
            }

            if (higherLocation == Vector.ZERO) {

                println("position: $position has no higher power at around")

                tickableBlocks.join(position, world, position, 0)
            }

        }

        if(currentPower == power) return


        metadatas.setMetadataKeyValue(PowerBlockMetadata(power))

        world.setBlock(position, block)

        updateClientBlock(world, listOf(position to block))

        for (neighborBlockPosition in filterSpreadSignal) {
            val neighborBlock = world.getBlock(neighborBlockPosition)

            val neighborPower = neighborBlock.getPower()

            if (neighborPower == spreadPower || (neighborPower > power && power != 0)) continue
//            tickableBlocks.join(neighborBlockPosition, world, position, spreadPower)

            tickableBlocks.blockBehaviorRepository.getBlockBehavior(neighborBlock.getMaterial())
                ?.updateBlock(world, neighborBlockPosition, position, tickableBlocks)
        }

    }*/




    override fun canApply(material: MaterialKey): Boolean {
        return material == Material.REDSTONE_WIRE
    }
}