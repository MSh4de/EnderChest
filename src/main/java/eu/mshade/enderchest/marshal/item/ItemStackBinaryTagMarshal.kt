package eu.mshade.enderchest.marshal.item

import eu.mshade.enderframe.item.ItemStack
import eu.mshade.enderframe.item.Material
import eu.mshade.enderframe.metadata.MetadataKeyValueBufferRegistry
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag

object ItemStackBinaryTagMarshal {

    fun serialize(itemStack: ItemStack?, metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry): CompoundBinaryTag {
        if (itemStack == null) {
            return CompoundBinaryTag()
        }
        val compoundBinaryTag = CompoundBinaryTag()
        compoundBinaryTag.putInt("type", itemStack.material.id)
        compoundBinaryTag.putInt("amount", itemStack.amount)
        compoundBinaryTag.putInt("durability", itemStack.durability)
        compoundBinaryTag.putBinaryTag("metadata", metadataKeyValueBufferRegistry.serialize(itemStack.metadatas))
        return compoundBinaryTag
    }

    fun deserialize(compoundBinaryTag: CompoundBinaryTag, metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry): ItemStack? {
        if (compoundBinaryTag.isEmpty()) {
            return null
        }
        
        val type = compoundBinaryTag.getInt("type")
        val amount = compoundBinaryTag.getInt("amount")
        val durability = compoundBinaryTag.getInt("durability")
        val metadata = metadataKeyValueBufferRegistry.deserialize(compoundBinaryTag.getBinaryTag("metadata") as CompoundBinaryTag)
        return ItemStack(Material.fromId(type), amount, durability, metadata)
    }

}