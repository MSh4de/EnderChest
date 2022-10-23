package eu.mshade.enderchest.marshal.metadata

import eu.mshade.enderframe.metadata.MetadataKeyValue
import eu.mshade.enderframe.world.Difficulty
import eu.mshade.enderframe.world.Dimension
import eu.mshade.enderframe.world.LevelType
import eu.mshade.enderframe.world.metadata.*
import eu.mshade.mwork.binarytag.BinaryTag
import eu.mshade.mwork.binarytag.BinaryTagDriver
import eu.mshade.mwork.binarytag.entity.LongBinaryTag
import eu.mshade.mwork.binarytag.entity.StringBinaryTag

class NameWorldMetadataBuffer : MetadataKeyValueBuffer {

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        val stringBinaryTag = binaryTag as? StringBinaryTag
        return NameWorldMetadata(stringBinaryTag?.value)
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        val nameWorldMetadata = metadataKeyValue as? NameWorldMetadata
        return StringBinaryTag(nameWorldMetadata?.metadataValue)
    }

}

class SeedWorldMetadataBuffer : MetadataKeyValueBuffer {

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        val longBinaryTag = binaryTag as? LongBinaryTag
        return SeedWorldMetadata(longBinaryTag?.value)
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        val seedWorldMetadata = metadataKeyValue as? SeedWorldMetadata
        return LongBinaryTag(seedWorldMetadata?.metadataValue ?: 0)
    }

}

class DimensionWorldMetadataBuffer(val binaryTagDriver: BinaryTagDriver) : MetadataKeyValueBuffer {

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        return DimensionWorldMetadata(binaryTagDriver.unMarshal(binaryTag, Dimension::class.java))
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        return binaryTagDriver.marshal(metadataKeyValue.metadataValue)
    }

}

class LevelTypeWorldMetadataBuffer(val binaryTagDriver: BinaryTagDriver): MetadataKeyValueBuffer {

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        return LevelTypeWorldMetadata(binaryTagDriver.unMarshal(binaryTag, LevelType::class.java))
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        return binaryTagDriver.marshal(metadataKeyValue.metadataValue)
    }

}

class DifficultyWorldMetadataBuffer(val binaryTagDriver: BinaryTagDriver): MetadataKeyValueBuffer {

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        return DifficultyWorldMetadata(binaryTagDriver.unMarshal(binaryTag, Difficulty::class.java))
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        return binaryTagDriver.marshal(metadataKeyValue.metadataValue)
    }

}