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

class WorldBinaryTagMarshal {
    fun serialize(binaryTagDriver: BinaryTagDriver, world: World): CompoundBinaryTag {
        val metadataKeyValueBinaryTagMarshal: MetadataKeyValueBinaryTagMarshal = binaryTagDriver.getDynamicMarshal(
            MetadataKeyValueBinaryTagMarshal::class.java
        )
        return metadataKeyValueBinaryTagMarshal.serialize(world.metadataKeyValueBucket)
    }

    fun deserialize(
        binaryTagDriver: BinaryTagDriver,
        binaryTag: BinaryTag<*>?,
        worldManager: WorldManager?,
        worldFolder: File?
    ): World {
        val metadataKeyValueBinaryTagMarshal: MetadataKeyValueBinaryTagMarshal = binaryTagDriver.getDynamicMarshal(
            MetadataKeyValueBinaryTagMarshal::class.java
        )
        val metadataKeyValueBucket = metadataKeyValueBinaryTagMarshal.deserialize((binaryTag as CompoundBinaryTag?)!!)
        return DefaultWorld(worldManager, worldFolder, metadataKeyValueBucket)
    }

    fun write(binaryTagDriver: BinaryTagDriver, world: World) {
        if (world.metadataKeyValueBucket.consumeUpdatedMetadataKeyValue().isEmpty()) return
        try {
            val fileOutputStream = FileOutputStream(File(world.worldFolder, "level.dat"))
            binaryTagDriver.writeCompoundBinaryTag(serialize(binaryTagDriver, world), fileOutputStream)
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun read(binaryTagDriver: BinaryTagDriver, file: File?, worldManager: WorldManager?): World {
        val compoundBinaryTag = binaryTagDriver.readCompoundBinaryTag(File(file, "level.dat"))
        return deserialize(binaryTagDriver, compoundBinaryTag, worldManager, file)
    }
}