package eu.mshade.enderchest.marshal.item

import eu.mshade.enderchest.marshal.mojang.TextComponentBinaryTagMarshal
import eu.mshade.enderframe.item.metadata.NameItemStackMetadata
import eu.mshade.enderframe.metadata.MetadataKeyValue
import eu.mshade.enderframe.metadata.MetadataKeyValueBuffer
import eu.mshade.enderframe.mojang.chat.TextComponent
import eu.mshade.mwork.binarytag.BinaryTag
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag

class NameItemStackMetadataBuffer : MetadataKeyValueBuffer {

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        return NameItemStackMetadata(TextComponentBinaryTagMarshal.deserialize(binaryTag as CompoundBinaryTag))
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        return TextComponentBinaryTagMarshal.serialize((metadataKeyValue as NameItemStackMetadata).metadataValue)
    }

}
