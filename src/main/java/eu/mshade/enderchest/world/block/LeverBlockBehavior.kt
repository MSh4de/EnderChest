package eu.mshade.enderchest.world.block

import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.item.Material
import eu.mshade.enderframe.item.MaterialKey
import eu.mshade.enderframe.world.Vector
import eu.mshade.enderframe.world.World
import eu.mshade.enderframe.world.block.*
import eu.mshade.enderframe.world.block.redstone.Redstone

class LeverBlockBehavior : BlockBehavior {

    override fun place(
        player: Player,
        blockPosition: Vector,
        blockFace: BlockFace,
        cursorPosition: Vector,
        material: MaterialKey
    ): Collection<Pair<Vector, Block>> {
        val blocks = mutableListOf<Pair<Vector, Block>>()

        val location = player.getLocation()
        val block = material.toBlock()
        val metadataKeyValueBucket = block.getMetadatas()
        metadataKeyValueBucket.setMetadataKeyValue(FaceBlockMetadata(blockFace))
        metadataKeyValueBucket.setMetadataKeyValue(PowerBlockMetadata(false))
        metadataKeyValueBucket.setMetadataKeyValue(
            AxisBlockMetadata(
                BlockFace.fromDirection(location.yaw).toAxis()
            )
        )
        metadataKeyValueBucket.setMetadataKeyValue(RedstoneStateBlockMetadata(RedstoneState.EMITTING))
        blocks.add(blockPosition to block)
        return blocks
    }

    override fun interact(
        player: Player,
        blockPosition: Vector,
        blockFace: BlockFace,
        cursorPosition: Vector,
        block: Block,
        tickableBlocks: TickableBlockRepository
    ) {
        val location = player.getLocation()
        val world = location.world


        val metadatas = block.getMetadatas()
        val powered = block.hasPower()

        metadatas.setMetadataKeyValue(PowerBlockMetadata(!powered))


        world.setBlock(blockPosition, block)
        updateClientBlock(world, listOf(blockPosition to block))

        val blockUpdates = Redstone.spreadPower(blockPosition, block.getFace())
        val applySpreadPower = Redstone.filterSpreadSignal(blockPosition, world, blockUpdates)

        applySpreadPower.forEach {
            tickableBlocks.join(world, it, blockPosition)
        }

    }


    override fun canApply(material: MaterialKey): Boolean {
        return material == Material.LEVER
    }

}