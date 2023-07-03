package eu.mshade.enderchest.world.block

import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.item.Material
import eu.mshade.enderframe.item.MaterialKey
import eu.mshade.enderframe.world.Vector
import eu.mshade.enderframe.world.block.*

class BedBlockBehavior : BlockBehavior {

    override fun place(
        player: Player,
        blockPosition: Vector,
        blockFace: BlockFace,
        cursorPosition: Vector,
        material: MaterialKey
    ): Collection<Pair<Vector, Block>> {
        val blocks = mutableListOf<Pair<Vector, Block>>()

        val location = player.getLocation()
        val world = location.world
        val direction = BlockFace.fromDirection(location.yaw)
        val headPosition = blockPosition.clone().add(direction.vector)
        if (world.getBlock(headPosition).getMaterial() == Material.AIR && world.getBlock(blockPosition).getMaterial() == Material.AIR) {
            val headBlock = createBedBlock(material, direction, BlockHalf.TOP)
            val footBlock = createBedBlock(material, direction, BlockHalf.BOTTOM)
            blocks.add(blockPosition to footBlock)
            blocks.add(headPosition to headBlock)
        }else {
            blocks.add(blockPosition to Material.AIR.toBlock())
        }

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

        player.minecraftSession.sendMessage("You interacted with a bed!")

    }

    override fun canApply(material: MaterialKey): Boolean {
        return material.namespacedKey.key.endsWith("_bed")
    }

    private fun createBedBlock(material: MaterialKey, blockFace: BlockFace, blockHalf: BlockHalf): Block {
        val block = material.toBlock()
        val metadataKeyValueBucket = block.getMetadatas()
        metadataKeyValueBucket.setMetadataKeyValue(FaceBlockMetadata(blockFace))
        metadataKeyValueBucket.setMetadataKeyValue(HalfBlockMetadata(blockHalf))
        return block
    }

}