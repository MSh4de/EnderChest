package eu.mshade.enderchest.world.blockrule

import eu.mshade.enderframe.item.Material
import eu.mshade.enderframe.item.MaterialKey
import eu.mshade.enderframe.world.Location
import eu.mshade.enderframe.world.Vector
import eu.mshade.enderframe.world.block.*

class VineBlockRule : BlockRule {

    override fun apply(
        pov: Location,
        blockPosition: Vector,
        blockFace: BlockFace,
        cursorPosition: Vector,
        block: Block
    ): Block {
        val previousBlock = pov.world.getBlock(blockPosition)

        val vineBlock : Block = if (previousBlock.getMaterialKey() == Material.VINE){
            previousBlock
        }else {
            block
        }

        val metadataKeyValueBucket = vineBlock.getMetadataKeyValueBucket()
        var metadataKeyValue = metadataKeyValueBucket.getMetadataKeyValue(BlockMetadataType.MULTIPLE_FACE) as? MultipleFaceBlockMetadata

        if (metadataKeyValue == null) {
            metadataKeyValue = MultipleFaceBlockMetadata()
            metadataKeyValueBucket.setMetadataKeyValue(metadataKeyValue)
        }

        metadataKeyValue.metadataValue.add(blockFace)

        return vineBlock
    }

    override fun canApply(material: MaterialKey): Boolean {
        return material == Material.VINE
    }

}