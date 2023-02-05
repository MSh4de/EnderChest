package eu.mshade.enderchest.marshal.item

import eu.mshade.enderchest.marshal.mojang.TextComponentBinaryTagMarshal
import eu.mshade.enderframe.item.metadata.LoreItemStackMetadata
import eu.mshade.enderframe.metadata.MetadataKeyValue
import eu.mshade.enderframe.metadata.MetadataKeyValueBuffer
import eu.mshade.enderframe.mojang.chat.TextComponent
import eu.mshade.mwork.binarytag.BinaryTag
import eu.mshade.mwork.binarytag.BinaryTagType
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import eu.mshade.mwork.binarytag.entity.ListBinaryTag

class LoreItemStackMetadataBuffer: MetadataKeyValueBuffer {

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        binaryTag as ListBinaryTag
        val lore = mutableListOf<TextComponent>()
        binaryTag.value.forEach {
            lore.add(TextComponentBinaryTagMarshal.deserialize(it as CompoundBinaryTag))
        }
        return LoreItemStackMetadata(lore)
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        val lore = mutableListOf<BinaryTag<*>>()
        (metadataKeyValue as LoreItemStackMetadata).metadataValue.forEach {
            lore.add(TextComponentBinaryTagMarshal.serialize(it))
        }
        return ListBinaryTag(BinaryTagType.COMPOUND, lore)
    }
}