package eu.mshade.enderchest.marshal.item

import eu.mshade.enderchest.marshal.mojang.TextComponentBinaryTagMarshal
import eu.mshade.enderframe.inventory.Inventory
import eu.mshade.enderframe.inventory.InventoryKey
import eu.mshade.enderframe.inventory.InventoryType
import eu.mshade.enderframe.inventory.NamedInventory
import eu.mshade.enderframe.inventory.PlayerInventory
import eu.mshade.enderframe.inventory.type.ChestInventory
import eu.mshade.enderframe.metadata.MetadataKeyValueBufferRegistry
import eu.mshade.enderframe.mojang.chat.TextComponent
import eu.mshade.mwork.binarytag.BinaryTagType
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import eu.mshade.mwork.binarytag.entity.ListBinaryTag
import java.util.UUID
import java.util.function.Supplier

object InventoryBinaryTagMarshal {

    private val INVENTORY_BY_ID = mutableMapOf<InventoryKey, (txt: TextComponent, size: Int) -> Inventory>()

    init {
        INVENTORY_BY_ID[InventoryType.CHEST] = {txt, size -> ChestInventory(txt, arrayOfNulls(size))}
        INVENTORY_BY_ID[InventoryType.PLAYER] = { _, _ -> PlayerInventory() }
    }

    fun serialize(inventory: Inventory, metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry): CompoundBinaryTag{
        val compoundBinaryTag = CompoundBinaryTag()
        compoundBinaryTag.putInt("type", inventory.inventoryKey.id)
        compoundBinaryTag.putInt("size", inventory.size)

        if (inventory is NamedInventory){
            compoundBinaryTag.putBinaryTag("name", TextComponentBinaryTagMarshal.serialize(inventory.name))
        }

        val itemListBinaryTag = ListBinaryTag(BinaryTagType.COMPOUND)
        inventory.itemStacks.forEach {
            itemListBinaryTag.add(ItemStackBinaryTagMarshal.serialize(it, metadataKeyValueBufferRegistry))
        }

        compoundBinaryTag.putBinaryTag("items", itemListBinaryTag)
        return compoundBinaryTag
    }

    fun deserialize(compoundBinaryTag: CompoundBinaryTag, metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry): Inventory{
        val type = InventoryType.fromId(compoundBinaryTag.getInt("type"))!!
        val size = compoundBinaryTag.getInt("size")
        val nameBinaryTag = compoundBinaryTag.getBinaryTag("name")
        val name = if (nameBinaryTag != null) TextComponentBinaryTagMarshal.deserialize(nameBinaryTag as CompoundBinaryTag) else TextComponent.empty()
        val inventory = INVENTORY_BY_ID[type]?.invoke(name, size) ?: NamedInventory(name, type, arrayOfNulls(size))

        val itemListBinaryTag = compoundBinaryTag.getBinaryTag("items") as ListBinaryTag
        itemListBinaryTag.value.forEachIndexed() { index, binaryTag ->
            inventory.setItemStack(index, ItemStackBinaryTagMarshal.deserialize(binaryTag as CompoundBinaryTag, metadataKeyValueBufferRegistry))
        }
        return inventory
    }
}