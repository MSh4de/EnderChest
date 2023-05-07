package eu.mshade.enderchest.world.blockrule

import eu.mshade.enderframe.item.Material
import eu.mshade.enderframe.world.Location
import eu.mshade.enderframe.world.Vector
import eu.mshade.enderframe.world.block.*

class SlabBlockRule : BlockRule() {

    override fun apply(
        pov: Location,
        blockPosition: Vector,
        blockFace: BlockFace,
        cursorPosition: Vector,
        block: Block
    ): Block {
        val metadataKeyValueBucket = block.getMetadataKeyValueBucket()
        val blockHalf = BlockHalf.fromY(cursorPosition.y)

        var slabType: SlabType? = null
        if (blockHalf == BlockHalf.TOP) slabType = SlabType.TOP
        else if (blockHalf == BlockHalf.BOTTOM) slabType = SlabType.BOTTOM

        metadataKeyValueBucket.setMetadataKeyValue(SlabTypeBlockMetadata(slabType!!))

        val oppositeBlock = pov.world.getBlock(blockPosition.clone().add(blockFace.oppositeFace.vector))
        val previousBlock = pov.world.getBlock(blockPosition)

        if ((blockFace == BlockFace.UP || blockFace == BlockFace.DOWN)) {
            if (oppositeBlock.getMaterialKey() == block.getMaterialKey()) {
                blockPosition.add(blockFace.oppositeFace.vector)
                metadataKeyValueBucket.setMetadataKeyValue(SlabTypeBlockMetadata(SlabType.DOUBLE))
            }else {
                if (previousBlock.getMaterialKey() == block.getMaterialKey()) {
                    metadataKeyValueBucket.setMetadataKeyValue(SlabTypeBlockMetadata(SlabType.DOUBLE))
                }else {
                    metadataKeyValueBucket.setMetadataKeyValue(SlabTypeBlockMetadata(SlabType.BOTTOM))
                }
            }
        }else if (previousBlock.getMaterialKey() == block.getMaterialKey()) {
            metadataKeyValueBucket.setMetadataKeyValue(SlabTypeBlockMetadata(SlabType.DOUBLE))
        }

        if (previousBlock.getMaterialKey() == Material.AIR) return block

        return block
    }

}