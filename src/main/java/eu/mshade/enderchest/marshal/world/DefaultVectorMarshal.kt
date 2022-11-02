package eu.mshade.enderchest.marshal.world

import eu.mshade.enderframe.world.Vector
import eu.mshade.mwork.binarytag.BinaryTag
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag

class DefaultVectorMarshal {
    fun serialize(vector: Vector): BinaryTag<*> {
        val compoundBinaryTag = CompoundBinaryTag()
        compoundBinaryTag.putDouble("x", vector.x)
        compoundBinaryTag.putDouble("y", vector.y)
        compoundBinaryTag.putDouble("z", vector.z)
        return compoundBinaryTag
    }

    fun deserialize(binaryTag: BinaryTag<*>): Vector {
        val compoundBinaryTag = binaryTag as CompoundBinaryTag
        val x = compoundBinaryTag.getDouble("x")
        val y = compoundBinaryTag.getDouble("y")
        val z = compoundBinaryTag.getDouble("z")
        return Vector(x, y, z)
    }
}