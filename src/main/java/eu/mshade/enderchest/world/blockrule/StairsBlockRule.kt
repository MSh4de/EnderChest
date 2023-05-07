package eu.mshade.enderchest.world.blockrule

import eu.mshade.enderframe.world.Location
import eu.mshade.enderframe.world.Vector
import eu.mshade.enderframe.world.block.*

class StairsBlockRule : BlockRule() {

    override fun apply(
        pov: Location,
        blockPosition: Vector,
        blockFace: BlockFace,
        cursorPosition: Vector,
        block: Block
    ): Block {
        val metadataKeyValueBucket = block.getMetadataKeyValueBucket()
        val direction = BlockFace.fromDirection(pov.yaw)
        metadataKeyValueBucket.setMetadataKeyValue(FaceBlockMetadata(direction))
        metadataKeyValueBucket.setMetadataKeyValue(HalfBlockMetadata(BlockHalf.fromY(cursorPosition.y)))
        return block
    }

}