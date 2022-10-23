package eu.mshade.enderchest.marshal.metadata

import eu.mshade.enderframe.metadata.MetadataKeyValue
import eu.mshade.mwork.binarytag.BinaryTag

interface MetadataKeyValueBuffer {

    fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*>

    fun write(metadataKeyValue: MetadataKeyValue<*>) : BinaryTag<*>

}