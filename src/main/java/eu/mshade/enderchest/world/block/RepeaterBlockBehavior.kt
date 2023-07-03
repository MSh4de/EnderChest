package eu.mshade.enderchest.world.block

import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.item.Material
import eu.mshade.enderframe.item.MaterialKey
import eu.mshade.enderframe.world.Vector
import eu.mshade.enderframe.world.World
import eu.mshade.enderframe.world.block.*
import org.slf4j.LoggerFactory

class RepeaterBlockBehavior : BlockBehavior {

    companion object {
        val LOGGER = LoggerFactory.getLogger(RepeaterBlockBehavior::class.java)
    }

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
        val fromDirection = BlockFace.fromDirection(location.yaw).oppositeFace

        metadataKeyValueBucket.setMetadataKeyValue(FaceBlockMetadata(fromDirection))
        metadataKeyValueBucket.setMetadataKeyValue(DelayBlockMetadata(0))
        metadataKeyValueBucket.setMetadataKeyValue(TickableBlockMetadata(true))
        metadataKeyValueBucket.setMetadataKeyValue(RedstoneStateBlockMetadata(RedstoneState.REPLICATION))

        blocks.add(Pair(blockPosition, block))

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
        val delay = metadatas.getMetadataKeyValue(BlockMetadataType.DELAY)?.metadataValue as? Int ?: 0

        metadatas.setMetadataKeyValue(DelayBlockMetadata((delay + 1) and 3))

        world.setBlock(blockPosition, block)

        updateClientBlock(world, listOf(Pair(blockPosition, block)))

    }

    override fun updateBlock(world: World, position: Vector, previousPosition: Vector, tickableBlocks: TickableBlockRepository) {
        val block = world.getBlock(position)
        val metadatas = block.getMetadatas()
        val isPowered = block.hasPower()
        val delay = metadatas.getMetadataKeyValue(BlockMetadataType.DELAY)?.metadataValue as? Int ?: 0
        val tick = block.getTick()

        val behindBlock = world.getBlock(position.clone().add(block.getFace().vector))


        if (delay + 1 != tick) {
            block.modifyTick { it + 1 }
            world.setBlock(position, block)
            tickableBlocks.join(world, position, previousPosition)
            return
        }

        block.resetTick()

        if (isPowered){

            metadatas.setMetadataKeyValue(PowerBlockMetadata(false))

            world.setBlock(position, block)
            updateClientBlock(world, listOf(Pair(position, block)))

            updateFrontBlock(world, position, block, tickableBlocks)

        } else {
            metadatas.setMetadataKeyValue(PowerBlockMetadata(true))

            world.setBlock(position, block)
            updateClientBlock(world, listOf(Pair(position, block)))

            updateFrontBlock(world, position, block, tickableBlocks)

            if (!behindBlock.hasPower()){
                tickableBlocks.join(world, position, previousPosition)
            }

        }

    }

    fun updateFrontBlock(world: World, position: Vector, block: Block, tickableBlocks: TickableBlockRepository){
        val frontBlockPosition = position.clone().add(block.getFace().oppositeFace.vector)
        val frontBlock = world.getBlock(frontBlockPosition)

        if (frontBlock.isTickable()) {
            tickableBlocks.blockBehaviorRepository.getBlockBehavior(frontBlock.getMaterial())
                ?.updateBlock(world, frontBlockPosition, position, tickableBlocks)
        }
    }


    override fun canApply(material: MaterialKey): Boolean {
        return material == Material.REPEATER
    }
}