package eu.mshade.enderchest.marshal.world

import eu.mshade.enderchest.marshal.metadata.MetadataKeyValueBinaryTagMarshal
import eu.mshade.enderchest.world.DefaultChunk
import eu.mshade.enderframe.world.World
import eu.mshade.enderframe.world.chunk.Chunk
import eu.mshade.mwork.binarytag.*
import eu.mshade.mwork.binarytag.carbon.CarbonBinaryTag
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import eu.mshade.mwork.binarytag.entity.ListBinaryTag
import java.io.IOException
import java.util.concurrent.ExecutionException

object ChunkBinaryTagMarshal {

    fun serialize(
        chunk: Chunk,
        metadataKeyValueBinaryTagMarshal: MetadataKeyValueBinaryTagMarshal
    ): CompoundBinaryTag {
        val compoundBinaryTag: CompoundBinaryTag = ZstdCompoundBinaryTag()
        compoundBinaryTag.putInt("x", chunk.x)
        compoundBinaryTag.putInt("z", chunk.z)
        compoundBinaryTag.putBinaryTag("biomes", ByteArrayBinaryTag(chunk.biomes))
        val listBinaryTagSections = ListBinaryTag(BinaryTagType.COMPOUND)
        val listBinaryTagEntities = ListBinaryTag(BinaryTagType.COMPOUND)
        for (section in chunk.sections) {
            if (section != null) listBinaryTagSections.add(
                SectionBinaryTagMarshal.serialize(
                    section,
                    metadataKeyValueBinaryTagMarshal
                )
            )
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
        world: World,
        metadataKeyValueBinaryTagMarshal: MetadataKeyValueBinaryTagMarshal
    ): Chunk {
        val compoundBinaryTag = binaryTag as CompoundBinaryTag?

        val x = compoundBinaryTag!!.getInt("x")
        val z = compoundBinaryTag.getInt("z")
        val biome = compoundBinaryTag.getByteArray("biomes")
        val sectionBinaryTags = compoundBinaryTag.getBinaryTag("sections") as ListBinaryTag
        val entityBinaryTags = compoundBinaryTag.getBinaryTag("entities") as ListBinaryTag
        val chunk: Chunk = DefaultChunk(x, z, world, biome)
        val sections = chunk.sections
        sectionBinaryTags.value.forEach { sectionBinaryTag ->
            val section =
                SectionBinaryTagMarshal.deserialize(sectionBinaryTag, chunk, metadataKeyValueBinaryTagMarshal)
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

    private fun regionId(chunk: Chunk): String {
        return regionId(chunk.x, chunk.z)
    }

    private fun regionId(chunkX: Int, chunkZ: Int): String {
        return (chunkX shr 5).toString() + "," + (chunkZ shr 5)
    }

    private fun chunkId(chunk: Chunk): String {
        return chunk.x.toString() + "," + chunk.z
    }

    private fun chunkId(chunkX: Int, chunkZ: Int): String {
        return "$chunkX,$chunkZ"
    }

    fun write(
        binaryTagDriver: BinaryTagDriver,
        carbonBinaryTag: CarbonBinaryTag,
        chunk: Chunk,
        metadataKeyValueBinaryTagMarshal: MetadataKeyValueBinaryTagMarshal
    ) {
        try {
            carbonBinaryTag.writeCompoundBinaryTag(
                chunkId(chunk),
                this.serialize(chunk, metadataKeyValueBinaryTagMarshal)
            )
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun read(
        carbonBinaryTag: CarbonBinaryTag,
        world: World,
        chunkX: Int,
        chunkZ: Int,
        metadataKeyValueBinaryTagMarshal: MetadataKeyValueBinaryTagMarshal
    ): Chunk {
        val chunkId = chunkId(chunkX, chunkZ)
        val compoundBinaryTag = carbonBinaryTag.readCompoundBinaryTag(chunkId)
        return deserialize(compoundBinaryTag!!, world, metadataKeyValueBinaryTagMarshal)
    }

}