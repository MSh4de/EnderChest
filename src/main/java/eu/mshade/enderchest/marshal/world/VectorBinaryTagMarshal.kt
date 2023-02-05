package eu.mshade.enderchest.marshal.world

import eu.mshade.enderframe.world.Vector
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag

object VectorBinaryTagMarshal {

    fun serialize(vector: Vector): CompoundBinaryTag {
        val compound = CompoundBinaryTag()
        compound.putDouble("x", vector.x)
        compound.putDouble("y", vector.y)
        compound.putDouble("z", vector.z)
        return compound
    }

    fun deserialize(compoundBinaryTag: CompoundBinaryTag): Vector {
        return Vector(compoundBinaryTag.getDouble("x"), compoundBinaryTag.getDouble("y"), compoundBinaryTag.getDouble("z"))
    }
}