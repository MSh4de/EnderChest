package eu.mshade.enderchest.world.block

import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.inventory.Inventory
import eu.mshade.enderframe.inventory.InventoryTracker
import eu.mshade.enderframe.inventory.InventoryType
import eu.mshade.enderframe.inventory.NamedInventory
import eu.mshade.enderframe.item.MaterialKey
import eu.mshade.enderframe.mojang.chat.TextComponent
import eu.mshade.enderframe.world.Vector
import eu.mshade.enderframe.world.block.*

class ChestBlockBehavior : BlockBehavior {

    override fun place(
        player: Player,
        blockPosition: Vector,
        blockFace: BlockFace,
        cursorPosition: Vector,
        material: MaterialKey
    ): Collection<Pair<Vector, Block>> {
        val blocks = mutableListOf<Pair<Vector, Block>>()

        val location = player.getLocation()
        val block = material.toBlock()
        val metadataKeyValueBucket = block.getMetadatas()
        val fromDirection = BlockFace.fromDirection(location.yaw).oppositeFace

        metadataKeyValueBucket.setMetadataKeyValue(FaceBlockMetadata(fromDirection))
        val namedInventory = NamedInventory(TextComponent.empty(), InventoryType.CHEST)
        InventoryTracker.add(namedInventory)
        metadataKeyValueBucket.setMetadataKeyValue(InventoryBlockMetadata(namedInventory))

        blocks.add(Pair(blockPosition, block))

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

        val metadata = block.getMetadatas()
        var namedInventory = metadata.getMetadataKeyValue(BlockMetadataType.INVENTORY)?.metadataValue as? NamedInventory
        if (namedInventory == null) {
            namedInventory = NamedInventory(TextComponent.empty(), InventoryType.CHEST)
            InventoryTracker.add(namedInventory)
            metadata.setMetadataKeyValue(InventoryBlockMetadata(namedInventory))
        }


        player.openInventory(namedInventory)
    }

    override fun canApply(material: MaterialKey): Boolean {
        return material.namespacedKey.key.endsWith("_chest") || material.namespacedKey.key.startsWith("chest")
    }
}