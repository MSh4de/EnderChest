package eu.mshade.enderchest.marshal.metadata

import eu.mshade.enderframe.metadata.MetadataKeyValue
import eu.mshade.enderframe.metadata.MetadataKeyValueBuffer
import eu.mshade.enderframe.world.block.*
import eu.mshade.mwork.binarytag.*
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import eu.mshade.mwork.binarytag.entity.ListBinaryTag
import java.util.StringJoiner

class ExtraBlockMetadataBuffer: MetadataKeyValueBuffer {

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        return ExtraBlockMetadata(binaryTag as CompoundBinaryTag)
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        return (metadataKeyValue as ExtraBlockMetadata).metadataValue
    }

}

class FaceBlockMetadataBuffer: MetadataKeyValueBuffer{

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        return FaceBlockMetadata(BlockFace.fromId(binaryTag.value as Int))
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        return IntBinaryTag((metadataKeyValue as FaceBlockMetadata).metadataValue.id)
    }

}

class HalfBlockMetadataBuffer: MetadataKeyValueBuffer{

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        return HalfBlockMetadata(BlockHalf.fromId(binaryTag.value as Int))
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        return IntBinaryTag((metadataKeyValue as HalfBlockMetadata).metadataValue.id)
    }

}

class ShapeBlockMetadataBuffer: MetadataKeyValueBuffer{

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        return ShapeBlockMetadata(BlockShape.fromId(binaryTag.value as Int))
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        return IntBinaryTag((metadataKeyValue as ShapeBlockMetadata).metadataValue.id)
    }

}

class AxisBlockMetadataBuffer: MetadataKeyValueBuffer{

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        return AxisBlockMetadata(BlockAxis.fromId(binaryTag.value as Int))
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        return IntBinaryTag((metadataKeyValue as AxisBlockMetadata).metadataValue.id)
    }

}

class PoweredBlockMetadataBuffer: MetadataKeyValueBuffer{

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        return PoweredBlockMetadata(binaryTag.value as Boolean)
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        return BooleanBinaryTag((metadataKeyValue as PoweredBlockMetadata).metadataValue)
    }

}


class PowerBlockMetadataBuffer: MetadataKeyValueBuffer{

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        return PowerBlockMetadata(binaryTag.value as Int)
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        return IntBinaryTag((metadataKeyValue as PowerBlockMetadata).metadataValue)
    }

}

class DecayableBlockMetadataBuffer: MetadataKeyValueBuffer{

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        return DecayableBlockMetadata(binaryTag.value as Boolean)
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        return BooleanBinaryTag((metadataKeyValue as DecayableBlockMetadata).metadataValue)
    }

}

class CheckDecayBlockMetadataBuffer: MetadataKeyValueBuffer{

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        return CheckDecayBlockMetadata(binaryTag.value as Boolean)
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        return BooleanBinaryTag((metadataKeyValue as CheckDecayBlockMetadata).metadataValue)
    }

}

class SeamlessBlockMetadataBuffer: MetadataKeyValueBuffer{

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        return SeamlessBlockMetadata(binaryTag.value as Boolean)
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        return BooleanBinaryTag((metadataKeyValue as SeamlessBlockMetadata).metadataValue)
    }

}


class MultipleFaceBlockMetadataBuffer: MetadataKeyValueBuffer{

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        binaryTag as ListBinaryTag
        val list = mutableSetOf<BlockFace>()
        for (binaryTag1 in binaryTag.value) {
            list.add(BlockFace.fromId(binaryTag1.value as Int))
        }
        return MultipleFaceBlockMetadata(list)
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        val list = mutableListOf<BinaryTag<*>>()
        for (blockFace in (metadataKeyValue as MultipleFaceBlockMetadata).metadataValue) {
            list.add(IntBinaryTag(blockFace.id))
        }
        return ListBinaryTag(BinaryTagType.INT, list)
    }

}

class SlabTypeBlockMetadataBuffer: MetadataKeyValueBuffer{

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        return SlabTypeBlockMetadata(SlabType.fromId(binaryTag.value as Int))
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        return IntBinaryTag((metadataKeyValue as SlabTypeBlockMetadata).metadataValue.ordinal)
    }

}

