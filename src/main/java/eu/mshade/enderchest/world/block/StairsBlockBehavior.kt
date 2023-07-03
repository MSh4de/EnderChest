package eu.mshade.enderchest.world.block

import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.item.MaterialKey
import eu.mshade.enderframe.world.Vector
import eu.mshade.enderframe.world.block.*

class StairsBlockBehavior : BlockBehavior {

    override fun place(
        player: Player,
        blockPosition: Vector,
        blockFace: BlockFace,
        cursorPosition: Vector,
        material: MaterialKey
    ): Collection<Pair<Vector, Block>> {
        val collection = mutableListOf<Pair<Vector, Block>>()

        val location = player.getLocation()
        val block = material.toBlock()
        val metadataKeyValueBucket = block.getMetadatas()
        val direction = BlockFace.fromDirection(location.yaw)
        metadataKeyValueBucket.setMetadataKeyValue(FaceBlockMetadata(direction))
        metadataKeyValueBucket.setMetadataKeyValue(HalfBlockMetadata(BlockHalf.fromY(cursorPosition.y)))

        collection.add(Pair(blockPosition, block))

        return collection
    }

    override fun canApply(material: MaterialKey): Boolean {
        return material.namespacedKey.key.endsWith("_stairs")
    }

}