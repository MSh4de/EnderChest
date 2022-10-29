package eu.mshade.enderchest.marshal.metadata

import eu.mshade.enderframe.metadata.MetadataKey
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket
import eu.mshade.enderframe.metadata.world.WorldMetadataType
import eu.mshade.enderframe.world.block.BlockMetadataType.AXIS
import eu.mshade.enderframe.world.block.BlockMetadataType.CHECK_DECAY
import eu.mshade.enderframe.world.block.BlockMetadataType.DECAYABLE
import eu.mshade.enderframe.world.block.BlockMetadataType.EXTRA
import eu.mshade.enderframe.world.block.BlockMetadataType.FACE
import eu.mshade.enderframe.world.block.BlockMetadataType.HALF
import eu.mshade.enderframe.world.block.BlockMetadataType.POWER
import eu.mshade.enderframe.world.block.BlockMetadataType.POWERED
import eu.mshade.enderframe.world.block.BlockMetadataType.SEAMLESS
import eu.mshade.enderframe.world.block.BlockMetadataType.SHAPE
import eu.mshade.mwork.binarytag.BinaryTag
import eu.mshade.mwork.binarytag.BinaryTagDriver
import eu.mshade.mwork.binarytag.BinaryTagDynamicMarshal
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import org.slf4j.LoggerFactory

class MetadataKeyValueBinaryTagMarshal(binaryTagDriver: BinaryTagDriver?) : BinaryTagDynamicMarshal {
    private val metadataKeyByName: MutableMap<String, MetadataKey> = HashMap()
    private val metadataKeyValueBuffer: MutableMap<MetadataKey?, MetadataKeyValueBuffer> = HashMap()

    init {
        register(WorldMetadataType.NAME, NameWorldMetadataBuffer())
        register(WorldMetadataType.SEED, SeedWorldMetadataBuffer())
        register(WorldMetadataType.DIMENSION, DimensionWorldMetadataBuffer(binaryTagDriver!!))
        register(WorldMetadataType.LEVEL_TYPE, LevelTypeWorldMetadataBuffer(binaryTagDriver))
        register(WorldMetadataType.DIFFICULTY, DifficultyWorldMetadataBuffer(binaryTagDriver))

        register(EXTRA, ExtraBlockMetadataBuffer())
        register(FACE, FaceBlockMetadataBuffer())
        register(HALF, HalfBlockMetadataBuffer())
        register(SHAPE, ShapeBlockMetadataBuffer())
        register(AXIS, AxisBlockMetadataBuffer())
        register(POWERED, PoweredBlockMetadataBuffer())
        register(POWER, PowerBlockMetadataBuffer())
        register(DECAYABLE, DecayableBlockMetadataBuffer())
        register(CHECK_DECAY, CheckDecayBlockMetadataBuffer())
        register(SEAMLESS, SeamlessBlockMetadataBuffer())

        LOGGER.info("Register {} metadataKeyValueBuffers", metadataKeyByName.size)
    }

    fun serialize(metadataKeyValueBucket: MetadataKeyValueBucket): CompoundBinaryTag {
        val compoundBinaryTag = CompoundBinaryTag()
        for (metadataKeyValue in metadataKeyValueBucket.metadataKeyValues) {
            compoundBinaryTag.putBinaryTag(
                metadataKeyValue.metadataKey.name, metadataKeyValueBuffer[metadataKeyValue.metadataKey]!!
                    .write(metadataKeyValue)
            )
        }
        return compoundBinaryTag
    }

    fun deserialize(compoundBinaryTag: CompoundBinaryTag): MetadataKeyValueBucket {
        val metadataKeyValueBucket = MetadataKeyValueBucket()
        compoundBinaryTag.value.forEach { (s: String, binaryTag: BinaryTag<*>?) ->
            val metadataKeyValue = metadataKeyValueBuffer[metadataKeyByName[s]]!!
                .read(binaryTag!!)
            metadataKeyValueBucket.setMetadataKeyValue(metadataKeyValue)
        }
        return metadataKeyValueBucket
    }

    fun <K : MetadataKey?> register(metadataKey: K, metadataKeyValueBuffer: MetadataKeyValueBuffer) {
        metadataKeyByName[metadataKey!!.name] = metadataKey
        this.metadataKeyValueBuffer[metadataKey] = metadataKeyValueBuffer
        LOGGER.debug("Register $metadataKey")
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MetadataKeyValueBinaryTagMarshal::class.java)
    }
}