package eu.mshade.enderchest.marshal.world.virtual

import eu.mshade.enderchest.marshal.metadata.MetadataKeyValueBinaryTagMarshal
import eu.mshade.enderchest.world.ChunkSafeguard
import eu.mshade.enderchest.world.virtual.DefaultVirtualWorld
import eu.mshade.enderframe.virtualserver.VirtualWorld
import eu.mshade.mwork.binarytag.*
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import java.io.File

object VirtualWorldBinaryTagMarshal {



    fun deserialize(binaryTag: BinaryTag<*>, chunkSafeguard: ChunkSafeguard, worldFolder: File, metadataKeyValueBinaryTagMarshal: MetadataKeyValueBinaryTagMarshal): VirtualWorld {
        val metadataKeyValueBucket = metadataKeyValueBinaryTagMarshal.deserialize((binaryTag as CompoundBinaryTag?)!!)
        return DefaultVirtualWorld(chunkSafeguard, worldFolder, metadataKeyValueBucket)
    }

    fun read(
        binaryTagDriver: BinaryTagDriver,
        chunkSafeguard: ChunkSafeguard,
        file: File,
        metadataKeyValueBinaryTagMarshal: MetadataKeyValueBinaryTagMarshal
    ): VirtualWorld {
        val compoundBinaryTag = binaryTagDriver.readCompoundBinaryTag(File(file, "level.dat"))
        return deserialize(compoundBinaryTag, chunkSafeguard, file, metadataKeyValueBinaryTagMarshal)
    }
}