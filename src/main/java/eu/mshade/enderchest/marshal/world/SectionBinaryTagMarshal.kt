package eu.mshade.enderchest.marshal.world

import eu.mshade.enderchest.marshal.metadata.MetadataKeyValueBinaryTagMarshal
import eu.mshade.enderchest.world.DefaultSection
import eu.mshade.enderframe.world.chunk.Chunk
import eu.mshade.enderframe.world.chunk.NibbleArray
import eu.mshade.enderframe.world.chunk.Section
import eu.mshade.mwork.binarytag.BinaryTag
import eu.mshade.mwork.binarytag.BinaryTagDriver
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag

object SectionBinaryTagMarshal {

    fun serialize(section: Section, metadataKeyValueBinaryTagMarshal: MetadataKeyValueBinaryTagMarshal): BinaryTag<*> {
        val compoundBinaryTag = CompoundBinaryTag()
        compoundBinaryTag.putInt("y", section.y)
        compoundBinaryTag.putInt("realBlock", section.realBlock)
        compoundBinaryTag.putBinaryTag("uniqueId", UniqueIdBinaryTagMarshal.serialize(section.uniqueId))
        compoundBinaryTag.putBinaryTag("palette", PaletteBinaryTagMarshal.serialize(section.palette, metadataKeyValueBinaryTagMarshal))
        compoundBinaryTag.putIntArray("blocks", section.blocks)
        compoundBinaryTag.putByteArray("blockLight", section.blockLight.rawData)
        compoundBinaryTag.putByteArray("skyLight", section.skyLight.rawData)
        return compoundBinaryTag
    }

    fun deserialize(binaryTag: BinaryTag<*>, chunk: Chunk, metadataKeyValueBinaryTagMarshal: MetadataKeyValueBinaryTagMarshal): Section {
        val compoundBinaryTag = binaryTag as CompoundBinaryTag
        val y = compoundBinaryTag.getInt("y")
        val realBlock = compoundBinaryTag.getInt("realBlock")
        val uniqueId = UniqueIdBinaryTagMarshal.deserialize(compoundBinaryTag.getBinaryTag("uniqueId")!!)
        val palette = PaletteBinaryTagMarshal.deserialize(compoundBinaryTag.getBinaryTag("palette")!!, metadataKeyValueBinaryTagMarshal)
        val blocks = compoundBinaryTag.getIntArray("blocks")
        val blockLight = NibbleArray(*compoundBinaryTag.getByteArray("blockLight"))
        val skyLight = NibbleArray(*compoundBinaryTag.getByteArray("skyLight"))
        return DefaultSection(chunk, y, realBlock, palette, blocks, uniqueId, blockLight, skyLight)
    }
}