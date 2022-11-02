package eu.mshade.enderchest.marshal.world

import eu.mshade.enderchest.world.DefaultSection
import eu.mshade.enderframe.world.chunk.Chunk
import eu.mshade.enderframe.world.chunk.NibbleArray
import eu.mshade.enderframe.world.chunk.Section
import eu.mshade.mwork.binarytag.BinaryTag
import eu.mshade.mwork.binarytag.BinaryTagDriver
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag

class SectionBinaryTagMarshal(binaryTagDriver: BinaryTagDriver) {
    private val paletteBinaryTagMarshal: PaletteBinaryTagMarshal
    private val uniqueIdBinaryTagMarshal: UniqueIdBinaryTagMarshal

    init {
        paletteBinaryTagMarshal = binaryTagDriver.getDynamicMarshal(PaletteBinaryTagMarshal::class.java)
        uniqueIdBinaryTagMarshal = binaryTagDriver.getDynamicMarshal(UniqueIdBinaryTagMarshal::class.java)
    }

    fun serialize(section: Section): BinaryTag<*> {
        val compoundBinaryTag = CompoundBinaryTag()
        compoundBinaryTag.putInt("y", section.y)
        compoundBinaryTag.putInt("realBlock", section.realBlock)
        compoundBinaryTag.putBinaryTag("uniqueId", uniqueIdBinaryTagMarshal.serialize(section.uniqueId))
        compoundBinaryTag.putBinaryTag("palette", paletteBinaryTagMarshal.serialize(section.palette))
        compoundBinaryTag.putIntArray("blocks", section.blocks)
        compoundBinaryTag.putByteArray("blockLight", section.blockLight.rawData)
        compoundBinaryTag.putByteArray("skyLight", section.skyLight.rawData)
        return compoundBinaryTag
    }

    fun deserialize(binaryTag: BinaryTag<*>, chunk: Chunk?): Section {
        val compoundBinaryTag = binaryTag as CompoundBinaryTag
        val y = compoundBinaryTag.getInt("y")
        val realBlock = compoundBinaryTag.getInt("realBlock")
        val uniqueId = uniqueIdBinaryTagMarshal.deserialize(compoundBinaryTag.getBinaryTag("uniqueId")!!)
        val palette = paletteBinaryTagMarshal.deserialize(compoundBinaryTag.getBinaryTag("palette")!!)
        val blocks = compoundBinaryTag.getIntArray("blocks")
        val blockLight = NibbleArray(*compoundBinaryTag.getByteArray("blockLight"))
        val skyLight = NibbleArray(*compoundBinaryTag.getByteArray("skyLight"))
        return DefaultSection(chunk, y, realBlock, palette, blocks, uniqueId, blockLight, skyLight)
    }
}