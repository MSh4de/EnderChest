package eu.mshade.enderchest.marshal.world.virtual

import eu.mshade.enderframe.metadata.MetadataKeyValueBufferRegistry
import eu.mshade.enderchest.marshal.world.PaletteBinaryTagMarshal
import eu.mshade.enderchest.marshal.world.UniqueIdBinaryTagMarshal
import eu.mshade.enderchest.world.virtual.VirtualChunk
import eu.mshade.enderchest.world.virtual.VirtualSection
import eu.mshade.enderchest.world.virtual.VirtualSectionStatus
import eu.mshade.enderframe.world.chunk.NibbleArray
import eu.mshade.enderframe.world.chunk.Section
import eu.mshade.mwork.binarytag.BinaryTag
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag

object VirtualSectionBinaryTagMarshal {


    fun serialize(virtualSection: VirtualSection, metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry): BinaryTag<*> {
        val compoundBinaryTag = CompoundBinaryTag()
        compoundBinaryTag.putInt("y", virtualSection.y)
        compoundBinaryTag.putString("status", virtualSection.virtualSectionStatus.name)
        compoundBinaryTag.putBinaryTag("uniqueId", UniqueIdBinaryTagMarshal.serialize(virtualSection.uniqueId))
        compoundBinaryTag.putBinaryTag("palette", PaletteBinaryTagMarshal.serialize(virtualSection.palette, metadataKeyValueBufferRegistry))
        compoundBinaryTag.putIntArray("blocks", virtualSection.blocks)
        compoundBinaryTag.putByteArray("blockLight", virtualSection.blockLight.rawData)
        compoundBinaryTag.putByteArray("skyLight", virtualSection.skyLight.rawData)
        return compoundBinaryTag
    }

    fun deserialize(binaryTag: BinaryTag<*>, chunk: VirtualChunk, metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry): Section {
        val compoundBinaryTag = binaryTag as CompoundBinaryTag
        val y = compoundBinaryTag.getInt("y")
        val uniqueId = UniqueIdBinaryTagMarshal.deserialize(compoundBinaryTag.getBinaryTag("uniqueId")!!)
        val palette = PaletteBinaryTagMarshal.deserialize(compoundBinaryTag.getBinaryTag("palette")!!, metadataKeyValueBufferRegistry)
        val blocks = compoundBinaryTag.getIntArray("blocks")
        val blockLight = NibbleArray(*compoundBinaryTag.getByteArray("blockLight"))
        val skyLight = NibbleArray(*compoundBinaryTag.getByteArray("skyLight"))
        val status = VirtualSectionStatus.valueOf(compoundBinaryTag.getString("status"))

        return VirtualSection(chunk, y, status, palette, blocks, uniqueId, blockLight, skyLight)
    }


}