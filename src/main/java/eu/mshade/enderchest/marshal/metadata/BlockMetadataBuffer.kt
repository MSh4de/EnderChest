package eu.mshade.enderchest.marshal.metadata

import eu.mshade.enderframe.metadata.MetadataKeyValue
import eu.mshade.enderframe.world.block.*
import eu.mshade.mwork.binarytag.BinaryTag
import eu.mshade.mwork.binarytag.entity.BooleanBinaryTag
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import eu.mshade.mwork.binarytag.entity.IntegerBinaryTag

class ExtraBlockMetadataBuffer: MetadataKeyValueBuffer{

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
        return IntegerBinaryTag((metadataKeyValue as FaceBlockMetadata).metadataValue.id)
    }

}

class HalfBlockMetadataBuffer: MetadataKeyValueBuffer{

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        return HalfBlockMetadata(BlockHalf.fromId(binaryTag.value as Int))
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        return IntegerBinaryTag((metadataKeyValue as HalfBlockMetadata).metadataValue.id)
    }

}

class ShapeBlockMetadataBuffer: MetadataKeyValueBuffer{

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        return ShapeBlockMetadata(BlockShape.fromId(binaryTag.value as Int))
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        return IntegerBinaryTag((metadataKeyValue as ShapeBlockMetadata).metadataValue.id)
    }

}

class AxisBlockMetadataBuffer: MetadataKeyValueBuffer{

    override fun read(binaryTag: BinaryTag<*>): MetadataKeyValue<*> {
        return AxisBlockMetadata(BlockAxis.fromId(binaryTag.value as Int))
    }

    override fun write(metadataKeyValue: MetadataKeyValue<*>): BinaryTag<*> {
        return IntegerBinaryTag((metadataKeyValue as AxisBlockMetadata).metadataValue.id)
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
        return IntegerBinaryTag((metadataKeyValue as PowerBlockMetadata).metadataValue)
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
