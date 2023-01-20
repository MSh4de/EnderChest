package eu.mshade.enderchest.marshal.metadata

import eu.mshade.enderframe.metadata.MetadataKeyValue
import eu.mshade.enderframe.metadata.MetadataKeyValueBuffer
import eu.mshade.enderframe.world.*
import eu.mshade.mwork.binarytag.BinaryTag
import eu.mshade.mwork.binarytag.BinaryTagDriver
import eu.mshade.mwork.binarytag.LongBinaryTag
import eu.mshade.mwork.binarytag.StringBinaryTag

class NameWorldMetadataBuffer : MetadataKeyValueBuffer {

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        val stringBinaryTag = binaryTag as? StringBinaryTag
        return NameWorldMetadata(stringBinaryTag!!.value)
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        val nameWorldMetadata = metadataKeyValue as? NameWorldMetadata
        return StringBinaryTag(nameWorldMetadata!!.metadataValue)
    }

}

class SeedWorldMetadataBuffer : MetadataKeyValueBuffer {

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        val longBinaryTag = binaryTag as? LongBinaryTag
        return SeedWorldMetadata(longBinaryTag!!.value)
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        val seedWorldMetadata = metadataKeyValue as? SeedWorldMetadata
        return LongBinaryTag(seedWorldMetadata?.metadataValue ?: 0)
    }

}

class DimensionWorldMetadataBuffer(val binaryTagDriver: BinaryTagDriver) : MetadataKeyValueBuffer {

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
       val dimension = Dimension.valueOf(binaryTag.value as String)
        return DimensionWorldMetadata(dimension)
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        val dimensionWorldMetadata = metadataKeyValue as? DimensionWorldMetadata
        return StringBinaryTag(dimensionWorldMetadata!!.metadataValue.name)
    }

}

class LevelTypeWorldMetadataBuffer(val binaryTagDriver: BinaryTagDriver): MetadataKeyValueBuffer {

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        val levelType = LevelType.valueOf(binaryTag.value as String)
        return LevelTypeWorldMetadata(levelType)
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        val levelTypeWorldMetadata = metadataKeyValue as? LevelTypeWorldMetadata
        return StringBinaryTag(levelTypeWorldMetadata!!.metadataValue.name)
    }

}

class DifficultyWorldMetadataBuffer(val binaryTagDriver: BinaryTagDriver): MetadataKeyValueBuffer {

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        val difficulty = Difficulty.valueOf(binaryTag.value as String)
        return DifficultyWorldMetadata(difficulty)
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        val difficultyWorldMetadata = metadataKeyValue as? DifficultyWorldMetadata
        return StringBinaryTag(difficultyWorldMetadata!!.metadataValue.name)
    }

}

class ParentWorldMetadataBuffer: MetadataKeyValueBuffer {

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        return ParentWorldMetadata(WorldRepository.getWorld(binaryTag.value as String))
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        return StringBinaryTag((metadataKeyValue.metadataValue as World).name)
    }
}