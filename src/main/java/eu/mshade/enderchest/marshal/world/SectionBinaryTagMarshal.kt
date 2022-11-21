package eu.mshade.enderchest.marshal.world

import eu.mshade.enderframe.metadata.MetadataKeyValueBufferRegistry
import eu.mshade.enderchest.world.DefaultSection
import eu.mshade.enderframe.world.chunk.Chunk
import eu.mshade.enderframe.world.chunk.NibbleArray
import eu.mshade.enderframe.world.chunk.Section
import eu.mshade.mwork.binarytag.BinaryTag
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag

object SectionBinaryTagMarshal {

    fun serialize(section: Section, metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry): BinaryTag<*> {
        val compoundBinaryTag = CompoundBinaryTag()
        compoundBinaryTag.putInt("y", section.y)
        compoundBinaryTag.putBinaryTag("uniqueId", UniqueIdBinaryTagMarshal.serialize(section.uniqueId))
        compoundBinaryTag.putBinaryTag("palette", PaletteBinaryTagMarshal.serialize(section.palette, metadataKeyValueBufferRegistry))
        compoundBinaryTag.putIntArray("blocks", section.blocks)
        compoundBinaryTag.putByteArray("blockLight", section.blockLight.rawData)
        compoundBinaryTag.putByteArray("skyLight", section.skyLight.rawData)
        return compoundBinaryTag
    }

    fun deserialize(binaryTag: BinaryTag<*>, chunk: Chunk, metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry): Section {
        val compoundBinaryTag = binaryTag as CompoundBinaryTag
        val y = compoundBinaryTag.getInt("y")
        val uniqueId = UniqueIdBinaryTagMarshal.deserialize(compoundBinaryTag.getBinaryTag("uniqueId")!!)
        val palette = PaletteBinaryTagMarshal.deserialize(compoundBinaryTag.getBinaryTag("palette")!!, metadataKeyValueBufferRegistry)
        val blocks = compoundBinaryTag.getIntArray("blocks")
        val blockLight = NibbleArray(*compoundBinaryTag.getByteArray("blockLight"))
        val skyLight = NibbleArray(*compoundBinaryTag.getByteArray("skyLight"))
        return DefaultSection(chunk, y, palette, blocks, uniqueId, blockLight, skyLight)
    }
}