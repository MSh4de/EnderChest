package eu.mshade.enderchest.world.blockrule

import eu.mshade.enderframe.item.MaterialKey
import eu.mshade.enderframe.world.Location
import eu.mshade.enderframe.world.Vector
import eu.mshade.enderframe.world.block.*

class LeverBlockRule : BlockRule {

    override fun apply(
        pov: Location,
        blockPosition: Vector,
        blockFace: BlockFace,
        cursorPosition: Vector,
        block: Block
    ): Block {
        val metadataKeyValueBucket = block.getMetadataKeyValueBucket()
        metadataKeyValueBucket.setMetadataKeyValue(FaceBlockMetadata(blockFace))
        metadataKeyValueBucket.setMetadataKeyValue(PoweredBlockMetadata(false))
        metadataKeyValueBucket.setMetadataKeyValue(
            AxisBlockMetadata(
                BlockFace.fromDirection(pov.yaw).toAxis()
            )
        )
        return block
    }

    override fun canApply(material: MaterialKey): Boolean {
        return material.namespacedKey.key.endsWith("_lever")
    }

}