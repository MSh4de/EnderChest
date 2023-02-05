package eu.mshade.enderchest.marshal.world.virtual

import eu.mshade.enderframe.metadata.MetadataKeyValueBufferRegistry
import eu.mshade.enderchest.world.ChunkSafeguard
import eu.mshade.enderchest.world.virtual.DefaultVirtualWorld
import eu.mshade.enderframe.virtualserver.VirtualWorld
import eu.mshade.mwork.binarytag.*
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import java.io.File

object VirtualWorldBinaryTagMarshal {



    fun deserialize(binaryTag: BinaryTag<*>, chunkSafeguard: ChunkSafeguard, worldFolder: File, metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry): VirtualWorld {
        val metadataKeyValueBucket = metadataKeyValueBufferRegistry.deserialize((binaryTag as CompoundBinaryTag?)!!)
        return DefaultVirtualWorld(chunkSafeguard, worldFolder, metadataKeyValueBucket)
    }

    fun read(
        binaryTagDriver: BinaryTagDriver,
        chunkSafeguard: ChunkSafeguard,
        file: File,
        metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry
    ): VirtualWorld {
        val compoundBinaryTag = binaryTagDriver.readCompoundBinaryTag(File(file, "level.dat"))
        return deserialize(compoundBinaryTag, chunkSafeguard, file, metadataKeyValueBufferRegistry)
    }
}