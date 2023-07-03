package eu.mshade.enderchest.world.block

import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.item.MaterialKey
import eu.mshade.enderframe.world.Vector
import eu.mshade.enderframe.world.block.AxisBlockMetadata
import eu.mshade.enderframe.world.block.Block
import eu.mshade.enderframe.world.block.BlockFace
import eu.mshade.enderframe.world.block.BlockBehavior

class WoodBlockBehavior : BlockBehavior {

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

        metadataKeyValueBucket.setMetadataKeyValue(
            AxisBlockMetadata(
                blockFace.toAxis()
            )
        )
        blocks.add(blockPosition to block)
        return blocks
    }


    override fun canApply(material: MaterialKey): Boolean {
        return material.namespacedKey.key.endsWith("_wood")
    }

}
