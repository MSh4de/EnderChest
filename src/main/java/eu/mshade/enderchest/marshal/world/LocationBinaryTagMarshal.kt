package eu.mshade.enderchest.marshal.world

import eu.mshade.enderchest.world.WorldManager
import eu.mshade.enderframe.metadata.world.WorldMetadataType
import eu.mshade.enderframe.world.Location
import eu.mshade.mwork.binarytag.BinaryTag
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag

class LocationBinaryTagMarshal {
    fun serialize(location: Location): BinaryTag<*> {
        val compoundBinaryTag = CompoundBinaryTag()
        val world = location.world
        val name = world.metadataKeyValueBucket.getValueOfMetadataKeyValue(WorldMetadataType.NAME, String::class.java)
        compoundBinaryTag.putString("world", name)
        compoundBinaryTag.putDouble("x", location.x)
        compoundBinaryTag.putDouble("y", location.y)
        compoundBinaryTag.putDouble("z", location.z)
        compoundBinaryTag.putFloat("yaw", location.yaw)
        compoundBinaryTag.putFloat("pitch", location.pitch)
        return compoundBinaryTag
    }

    fun deserialize(binaryTag: BinaryTag<*>, worldManager: WorldManager): Location {
        val compoundBinaryTag = binaryTag as CompoundBinaryTag
        val world = worldManager.getWorld(compoundBinaryTag.getString("world"))
        val x = compoundBinaryTag.getDouble("x")
        val y = compoundBinaryTag.getDouble("y")
        val z = compoundBinaryTag.getDouble("z")
        val yaw = compoundBinaryTag.getFloat("yaw")
        val pitch = compoundBinaryTag.getFloat("pitch")
        return Location(world, x, y, z, yaw, pitch)
    }
}