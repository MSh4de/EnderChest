package eu.mshade.enderchest.marshal.world

import eu.mshade.enderchest.marshal.metadata.MetadataKeyValueBinaryTagMarshal
import eu.mshade.enderframe.item.Material
import eu.mshade.enderframe.world.block.Block
import eu.mshade.enderframe.world.chunk.Palette
import eu.mshade.mwork.binarytag.BinaryTag
import eu.mshade.mwork.binarytag.BinaryTagDriver
import eu.mshade.mwork.binarytag.BinaryTagDynamicMarshal
import eu.mshade.mwork.binarytag.BinaryTagType
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import eu.mshade.mwork.binarytag.entity.ListBinaryTag

class PaletteBinaryTagMarshal(val binaryTagDriver: BinaryTagDriver): BinaryTagDynamicMarshal {

    private val metadataKeyValueBinaryTagMarshal = binaryTagDriver.getDynamicMarshal(MetadataKeyValueBinaryTagMarshal::class.java)

    fun serialize(palette: Palette): BinaryTag<*> {
        val paletteCompound = CompoundBinaryTag()
        val blockList = ListBinaryTag(BinaryTagType.COMPOUND)
        palette.blockById.forEach { (id, block) ->
            val blockCompound = CompoundBinaryTag()
            blockCompound.putInt("blockId", id);
            blockCompound.putInt("materialId", block.getMaterialKey().id);
            blockCompound.putInt("count", palette.getCount(id));
            blockCompound.putBinaryTag(
                "metadataKeyValueBucket",
                metadataKeyValueBinaryTagMarshal.serialize(block.getMetadataKeyValueBucket())
            )
            blockList.add(blockCompound)
        }
        paletteCompound.putBinaryTag("blocks", blockList)
        return paletteCompound
    }

    fun deserialize(binaryTag: BinaryTag<*>): Palette {
        val paletteCompound = binaryTag as CompoundBinaryTag
        val blockList = paletteCompound.getBinaryTag("blocks") as ListBinaryTag
        val palette = Palette()
        blockList.forEach { blockCompound ->
            val blockCompound = blockCompound as CompoundBinaryTag
            val blockId = blockCompound.getInt("blockId")
            val materialId = blockCompound.getInt("materialId")
            val count = blockCompound.getInt("count")
            val metadata = blockCompound.getBinaryTag("metadataKeyValueBucket") as CompoundBinaryTag
            val block = Block(Material.fromId(materialId), metadataKeyValueBinaryTagMarshal.deserialize(metadata))
            palette.setBlock(blockId, count, block)
        }
        return palette
    }

}