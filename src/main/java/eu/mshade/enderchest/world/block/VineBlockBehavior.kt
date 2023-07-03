package eu.mshade.enderchest.world.block

import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.item.Material
import eu.mshade.enderframe.item.MaterialKey
import eu.mshade.enderframe.world.Location
import eu.mshade.enderframe.world.Vector
import eu.mshade.enderframe.world.block.*

class VineBlockBehavior : BlockBehavior {

/*    override fun place(
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
    }*/

    override fun place(
        player: Player,
        blockPosition: Vector,
        blockFace: BlockFace,
        cursorPosition: Vector,
        material: MaterialKey
    ): Collection<Pair<Vector, Block>> {
        TODO("Not yet implemented")
    }





    override fun canApply(material: MaterialKey): Boolean {
        return material == Material.VINE
    }

}