package eu.mshade.enderchest.world.block

import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.item.Material
import eu.mshade.enderframe.item.MaterialKey
import eu.mshade.enderframe.world.Vector
import eu.mshade.enderframe.world.block.*

class SlabBlockBehavior : BlockBehavior {

    private val slabToDoubleSlab = mapOf(
        Material.OAK_SLAB to Material.DOUBLE_OAK_SLAB,
        Material.SPRUCE_SLAB to Material.DOUBLE_SPRUCE_SLAB,
        Material.BIRCH_SLAB to Material.DOUBLE_BIRCH_SLAB,
        Material.JUNGLE_SLAB to Material.DOUBLE_JUNGLE_SLAB,
        Material.ACACIA_SLAB to Material.DOUBLE_ACACIA_SLAB,
        Material.DARK_OAK_SLAB to Material.DOUBLE_DARK_OAK_SLAB,
        Material.STONE_SLAB to Material.DOUBLE_STONE_SLAB,
        Material.SMOOTH_STONE_SLAB to Material.SMOOTH_DOUBLE_STONE_SLAB,
        Material.SANDSTONE_SLAB to Material.DOUBLE_SANDSTONE_SLAB,
        Material.CUT_SANDSTONE_SLAB to Material.DOUBLE_CUT_SANDSTONE_SLAB,
        Material.PETRIFIED_OAK_SLAB to Material.DOUBLE_PETRIFIED_OAK_SLAB,
        Material.COBBLESTONE_SLAB to Material.DOUBLE_COBBLESTONE_SLAB,
        Material.BRICK_SLAB to Material.DOUBLE_BRICK_SLAB,
        Material.STONE_BRICK_SLAB to Material.DOUBLE_STONE_BRICK_SLAB,
        Material.NETHER_BRICK_SLAB to Material.DOUBLE_NETHER_BRICK_SLAB,
        Material.QUARTZ_SLAB to Material.DOUBLE_QUARTZ_SLAB,
        Material.RED_SANDSTONE_SLAB to Material.DOUBLE_RED_SANDSTONE_SLAB,
        Material.CUT_RED_SANDSTONE_SLAB to Material.DOUBLE_CUT_RED_SANDSTONE_SLAB,
        Material.PURPUR_SLAB to Material.DOUBLE_PURPUR_SLAB,
        Material.PRISMARINE_SLAB to Material.DOUBLE_PRISMARINE_SLAB,
        Material.PRISMARINE_BRICK_SLAB to Material.DOUBLE_PRISMARINE_BRICK_SLAB,
        Material.DARK_PRISMARINE_SLAB to Material.DOUBLE_DARK_PRISMARINE_SLAB,
        Material.SMOOTH_QUARTZ to Material.SMOOTH_DOUBLE_QUARTZ_SLAB,
        Material.RED_NETHER_BRICK_SLAB to Material.DOUBLE_RED_NETHER_BRICK_SLAB,
        Material.SMOOTH_RED_SANDSTONE to Material.SMOOTH_DOUBLE_RED_SANDSTONE_SLAB,
        Material.MOSSY_STONE_BRICK_SLAB to Material.DOUBLE_MOSSY_STONE_BRICK_SLAB,
        Material.MOSSY_COBBLESTONE_SLAB to Material.DOUBLE_MOSSY_COBBLESTONE_SLAB,
        Material.END_STONE_BRICK_SLAB to Material.DOUBLE_END_STONE_BRICK_SLAB,
        Material.SMOOTH_SANDSTONE to Material.SMOOTH_DOUBLE_SANDSTONE_SLAB,
    )

    override fun place(
        player: Player,
        blockPosition: Vector,
        blockFace: BlockFace,
        cursorPosition: Vector,
        material: MaterialKey
    ): Collection<Pair<Vector, Block>> {
        val collection = mutableListOf<Pair<Vector, Block>>()

        val blockHalf = BlockHalf.fromY(cursorPosition.y)

        val location = player.getLocation()
        val world = location.world


        val clickedBlock = world.getBlock(blockPosition.clone().add(blockFace.oppositeFace.vector))
        val previousBlock = world.getBlock(blockPosition)


        if (previousBlock.getMaterial() != material && clickedBlock.getMaterial() != material){
            val block = material.toBlock()
            val metadatas = block.getMetadatas()
            metadatas.setMetadataKeyValue(HalfBlockMetadata(blockHalf))
            collection.add(Pair(blockPosition, block))
        }

        if (blockFace == BlockFace.UP || blockFace == BlockFace.DOWN){
            if (previousBlock.getMaterial() == material){
                slabToDoubleSlab[material]?.let {
                    collection.add(Pair(blockPosition, Block(it)))
                }
            }
            if (clickedBlock.getMaterial() == material){
                slabToDoubleSlab[material]?.let {
                    collection.add(Pair(blockPosition.add(blockFace.oppositeFace.vector), Block(it)))
                }
            }
        }

        return collection
    }


    override fun canApply(material: MaterialKey): Boolean {
        return material.namespacedKey.key.endsWith("_slab")
    }

}