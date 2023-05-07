package eu.mshade.enderchest.world.blockrule

import eu.mshade.enderframe.world.Location
import eu.mshade.enderframe.world.Vector
import eu.mshade.enderframe.world.block.AxisBlockMetadata
import eu.mshade.enderframe.world.block.Block
import eu.mshade.enderframe.world.block.BlockFace
import eu.mshade.enderframe.world.block.BlockRule

class LogBlockRule : BlockRule() {

    override fun apply(
        pov: Location,
        blockPosition: Vector,
        blockFace: BlockFace,
        cursorPosition: Vector,
        block: Block
    ): Block {
        val metadataKeyValueBucket = block.getMetadataKeyValueBucket()

        metadataKeyValueBucket.setMetadataKeyValue(
            AxisBlockMetadata(
                blockFace.toAxis()
            )
        )
        return block
    }

}
