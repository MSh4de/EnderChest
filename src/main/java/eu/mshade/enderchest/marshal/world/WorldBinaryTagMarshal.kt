package eu.mshade.enderchest.marshal.world

import eu.mshade.enderchest.marshal.metadata.MetadataKeyValueBinaryTagMarshal
import eu.mshade.enderchest.world.DefaultWorld
import eu.mshade.enderchest.world.WorldManager
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
        metadataKeyValueBinaryTagMarshal: MetadataKeyValueBinaryTagMarshal
    ): CompoundBinaryTag {
        return metadataKeyValueBinaryTagMarshal.serialize(world.metadataKeyValueBucket)
    }

    fun deserialize(
        binaryTag: BinaryTag<*>,
        worldManager: WorldManager,
        worldFolder: File,
        metadataKeyValueBinaryTagMarshal: MetadataKeyValueBinaryTagMarshal
    ): World {
        val metadataKeyValueBucket =
            metadataKeyValueBinaryTagMarshal.deserialize((binaryTag as CompoundBinaryTag?)!!)
        return DefaultWorld(worldManager, worldFolder, metadataKeyValueBucket)
    }

    fun write(
        binaryTagDriver: BinaryTagDriver,
        world: World,
        metadataKeyValueBinaryTagMarshal: MetadataKeyValueBinaryTagMarshal
    ) {
        if (world.metadataKeyValueBucket.consumeUpdatedMetadataKeyValue().isEmpty()) return
        try {
            val fileOutputStream = FileOutputStream(File(world.worldFolder, "level.dat"))
            binaryTagDriver.writeCompoundBinaryTag(
                serialize(world, metadataKeyValueBinaryTagMarshal),
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
        worldManager: WorldManager,
        metadataKeyValueBinaryTagMarshal: MetadataKeyValueBinaryTagMarshal
    ): World {
        val compoundBinaryTag = binaryTagDriver.readCompoundBinaryTag(File(file, "level.dat"))
        return deserialize(compoundBinaryTag, worldManager, file, metadataKeyValueBinaryTagMarshal)
    }
}