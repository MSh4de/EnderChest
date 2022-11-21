package eu.mshade.enderchest.marshal.world.virtual

import eu.mshade.enderframe.metadata.MetadataKeyValueBufferRegistry
import eu.mshade.enderchest.world.virtual.VirtualChunk
import eu.mshade.enderchest.world.virtual.VirtualSection
import eu.mshade.enderframe.virtualserver.VirtualWorld
import eu.mshade.enderframe.world.chunk.Chunk
import eu.mshade.mwork.binarytag.BinaryTag
import eu.mshade.mwork.binarytag.BinaryTagType
import eu.mshade.mwork.binarytag.ByteArrayBinaryTag
import eu.mshade.mwork.binarytag.ZstdCompoundBinaryTag
import eu.mshade.mwork.binarytag.segment.SegmentBinaryTag
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import eu.mshade.mwork.binarytag.entity.ListBinaryTag
import java.io.IOException
import java.util.concurrent.ExecutionException

object VirtualChunkBinaryTagMarshal {


    fun write(
        segmentBinaryTag: SegmentBinaryTag,
        virtualChunk: VirtualChunk,
        metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry
    ) {
        try {
            segmentBinaryTag.writeCompoundBinaryTag(
                chunkId(virtualChunk.x, virtualChunk.z),
                serialize(virtualChunk, metadataKeyValueBufferRegistry)
            )
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun serialize(
        virtualChunk: VirtualChunk,
        metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry
    ): CompoundBinaryTag {
        val compoundBinaryTag: CompoundBinaryTag = ZstdCompoundBinaryTag()
        compoundBinaryTag.putInt("x", virtualChunk.x)
        compoundBinaryTag.putInt("z", virtualChunk.z)
        compoundBinaryTag.putBinaryTag("biomes", ByteArrayBinaryTag(virtualChunk.biomes))
        val listBinaryTagSections = ListBinaryTag(BinaryTagType.COMPOUND)
        val listBinaryTagEntities = ListBinaryTag(BinaryTagType.COMPOUND)
        for (section in virtualChunk.sections) {
            if (section != null) listBinaryTagSections.add(VirtualSectionBinaryTagMarshal.serialize(section as VirtualSection, metadataKeyValueBufferRegistry))
        }

        /*
    for(Entity entity : chunk.getEntities()) {
        listBinaryTagEntities.add(binaryTagDriver.marshal(entity));
    }

     */compoundBinaryTag.putBinaryTag("sections", listBinaryTagSections)
        compoundBinaryTag.putBinaryTag("entities", listBinaryTagEntities)
        return compoundBinaryTag
    }

    fun deserialize(
        binaryTag: BinaryTag<*>,
        virtualWorld: VirtualWorld,
        metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry
    ): Chunk {
        binaryTag as CompoundBinaryTag

        val x = binaryTag.getInt("x")
        val z = binaryTag.getInt("z")
        val biome = binaryTag.getByteArray("biomes")
        val sectionBinaryTags = binaryTag.getBinaryTag("sections") as ListBinaryTag
        val entityBinaryTags = binaryTag.getBinaryTag("entities") as ListBinaryTag
        val chunk = VirtualChunk(x, z, virtualWorld)
        val sections = chunk.sections
        sectionBinaryTags.value.forEach { sectionBinaryTag ->
            val section =
                VirtualSectionBinaryTagMarshal.deserialize(sectionBinaryTag, chunk, metadataKeyValueBufferRegistry)
            sections[section.y] = section
        }

        /*
    entityBinaryTags.forEach(entityBinaryTag ->{
        CompoundBinaryTag compoundBinaryTagEntity = (CompoundBinaryTag)entityBinaryTag;
        EntityType entityType = EntityType.getEntityTypeByName(compoundBinaryTagEntity.getString("entityType"));
        chunk.addEntity(binaryTagDriver.unMarshal(entityBinaryTag, entityType.getClazz()));
    });

     */return chunk
    }


    fun read(
        segmentBinaryTag: SegmentBinaryTag,
        virtualWorld: VirtualWorld,
        chunkX: Int,
        chunkZ: Int,
        metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry
    ): Chunk {
        val chunkId = chunkId(chunkX, chunkZ)
        val compoundBinaryTag = segmentBinaryTag.readCompoundBinaryTag(chunkId)
        return deserialize(compoundBinaryTag!!, virtualWorld, metadataKeyValueBufferRegistry)
    }

    private fun chunkId(chunkX: Int, chunkZ: Int): String {
        return "$chunkX,$chunkZ"
    }


}