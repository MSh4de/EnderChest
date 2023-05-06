package eu.mshade.enderchest.marshal.world

import eu.mshade.enderframe.metadata.MetadataKeyValueBufferRegistry
import eu.mshade.enderchest.world.ChunkSafeguard
import eu.mshade.enderchest.world.DefaultWorld
import eu.mshade.enderframe.world.World
import eu.mshade.mwork.binarytag.BinaryTag
import eu.mshade.mwork.binarytag.BinaryTagDriver
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object WorldBinaryTagMarshal {

    fun serialize(
        world: World,
        metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry
    ): CompoundBinaryTag {
        return metadataKeyValueBufferRegistry.serialize(world.metadataKeyValueBucket)
    }

    fun deserialize(
        binaryTag: BinaryTag<*>,
        chunkSafeguard: ChunkSafeguard,
        worldFolder: File,
        metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry
    ): World {
        val metadataKeyValueBucket =
            metadataKeyValueBufferRegistry.deserialize((binaryTag as CompoundBinaryTag?)!!)
        metadataKeyValueBucket.toggleTrackUpdates(true)
        return DefaultWorld(chunkSafeguard, worldFolder, metadataKeyValueBucket)
    }

    fun write(
        binaryTagDriver: BinaryTagDriver,
        world: World,
        metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry
    ) {
        if (world.metadataKeyValueBucket.consumeUpdatedMetadataKeyValue().isEmpty()) return
        try {
            val fileOutputStream = FileOutputStream(File(world.worldFolder, "level.dat"))
            binaryTagDriver.writeCompoundBinaryTag(
                serialize(world, metadataKeyValueBufferRegistry),
                fileOutputStream
            )
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun read(
        binaryTagDriver: BinaryTagDriver,
        file: File,
        chunkSafeguard: ChunkSafeguard,
        metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry
    ): World {
        val compoundBinaryTag = binaryTagDriver.readCompoundBinaryTag(File(file, "level.dat"))
        return deserialize(compoundBinaryTag, chunkSafeguard, file, metadataKeyValueBufferRegistry)
    }
}