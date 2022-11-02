package eu.mshade.enderchest.marshal.world

import eu.mshade.enderchest.world.DefaultChunk
import eu.mshade.enderframe.world.World
import eu.mshade.enderframe.world.chunk.Chunk
import eu.mshade.mwork.binarytag.*
import eu.mshade.mwork.binarytag.carbon.CarbonBinaryTag
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import eu.mshade.mwork.binarytag.entity.ListBinaryTag
import java.io.IOException
import java.util.concurrent.ExecutionException

class ChunkBinaryTagMarshal {
    fun serialize(binaryTagDriver: BinaryTagDriver, chunk: Chunk): CompoundBinaryTag {
        val compoundBinaryTag: CompoundBinaryTag = ZstdCompoundBinaryTag()
        compoundBinaryTag.putInt("x", chunk.x)
        compoundBinaryTag.putInt("z", chunk.z)
        compoundBinaryTag.putBinaryTag("biomes", ByteArrayBinaryTag(chunk.biomes))
        val listBinaryTagSections = ListBinaryTag(BinaryTagType.COMPOUND)
        val listBinaryTagEntities = ListBinaryTag(BinaryTagType.COMPOUND)
        val sectionBinaryTagMarshal: SectionBinaryTagMarshal = binaryTagDriver.getDynamicMarshal(
            SectionBinaryTagMarshal::class.java
        )
        for (section in chunk.sections) {
            if (section != null) listBinaryTagSections.add(sectionBinaryTagMarshal.serialize(section))
        }

        /*
        for(Entity entity : chunk.getEntities()) {
            listBinaryTagEntities.add(binaryTagDriver.marshal(entity));
        }

         */compoundBinaryTag.putBinaryTag("sections", listBinaryTagSections)
        compoundBinaryTag.putBinaryTag("entities", listBinaryTagEntities)
        return compoundBinaryTag
    }

    fun deserialize(binaryTagDriver: BinaryTagDriver, binaryTag: BinaryTag<*>?, world: World?): Chunk {
        val compoundBinaryTag = binaryTag as CompoundBinaryTag?
        val sectionBinaryTagMarshal: SectionBinaryTagMarshal = binaryTagDriver.getDynamicMarshal(
            SectionBinaryTagMarshal::class.java
        )
        val x = compoundBinaryTag!!.getInt("x")
        val z = compoundBinaryTag.getInt("z")
        val biome = compoundBinaryTag.getByteArray("biomes")
        val sectionBinaryTags = compoundBinaryTag.getBinaryTag("sections") as ListBinaryTag?
        val entityBinaryTags = compoundBinaryTag.getBinaryTag("entities") as ListBinaryTag?
        val chunk: Chunk = DefaultChunk(x, z, world, biome)
        val sections = chunk.sections
        sectionBinaryTags.forEach { sectionBinaryTag ->
            val section = sectionBinaryTagMarshal.deserialize(sectionBinaryTag, chunk)
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

    fun write(binaryTagDriver: BinaryTagDriver, carbonBinaryTag: CarbonBinaryTag, chunk: Chunk) {
        try {
            carbonBinaryTag.writeCompoundBinaryTag(chunkId(chunk), this.serialize(binaryTagDriver, chunk))
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    fun read(
        binaryTagDriver: BinaryTagDriver,
        carbonBinaryTag: CarbonBinaryTag,
        world: World?,
        chunkX: Int,
        chunkZ: Int
    ): Chunk {
        val chunkId = chunkId(chunkX, chunkZ)
        val compoundBinaryTag = carbonBinaryTag.readCompoundBinaryTag(chunkId)
        return deserialize(binaryTagDriver, compoundBinaryTag, world)
    }
}