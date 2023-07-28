package eu.mshade.enderchest.marshal.world

import eu.mshade.enderchest.EnderChest
import eu.mshade.enderframe.metadata.MetadataKeyValueBufferRegistry
import eu.mshade.enderchest.world.DefaultChunk
import eu.mshade.enderframe.MinecraftServer
import eu.mshade.enderframe.inventory.Inventory
import eu.mshade.enderframe.world.Vector
import eu.mshade.enderframe.world.World
import eu.mshade.enderframe.world.block.BlockMetadataType
import eu.mshade.enderframe.world.chunk.Chunk
import eu.mshade.mwork.binarytag.*
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import eu.mshade.mwork.binarytag.entity.ListBinaryTag
import eu.mshade.mwork.binarytag.segment.SegmentBinaryTag
import java.io.IOException
import java.util.concurrent.ExecutionException

object ChunkBinaryTagMarshal {

    val tickableBlocks = MinecraftServer.getTickableBlocks()

    fun serialize(
        chunk: Chunk,
        metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry
    ): CompoundBinaryTag {
        val compoundBinaryTag: CompoundBinaryTag = ZstdCompoundBinaryTag()
        compoundBinaryTag.putInt("x", chunk.x)
        compoundBinaryTag.putInt("z", chunk.z)
        compoundBinaryTag.putBinaryTag("biomes", ByteArrayBinaryTag(chunk.biomes))
        val listBinaryTagSections = ListBinaryTag(BinaryTagType.COMPOUND)
        val listBinaryTagEntities = ListBinaryTag(BinaryTagType.COMPOUND)

        for (i in chunk.sections.indices) {
            val section = chunk.sections[i]
            if (section != null) {
                listBinaryTagSections.add(SectionBinaryTagMarshal.serialize(section, metadataKeyValueBufferRegistry))
            }
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
        metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry
    ): Chunk {
        val compoundBinaryTag = binaryTag as CompoundBinaryTag?

        val x = compoundBinaryTag!!.getInt("x")
        val z = compoundBinaryTag.getInt("z")
        val biome = compoundBinaryTag.getByteArray("biomes")
        val sectionBinaryTags = compoundBinaryTag.getBinaryTag("sections") as ListBinaryTag
        val entityBinaryTags = compoundBinaryTag.getBinaryTag("entities") as ListBinaryTag
        val chunk: Chunk = DefaultChunk(x, z, world)
        chunk.biomes = biome
        val sections = chunk.sections
        sectionBinaryTags.value.forEach { sectionBinaryTag ->
            val section =
                SectionBinaryTagMarshal.deserialize(sectionBinaryTag, chunk, metadataKeyValueBufferRegistry)
            sections[section.y] = section
        }

        for (section in sections) {
            if (section == null) continue
            for (i in 0 until section.blocks.size) {
                val block = section.getBlock(i)

                if (block.isTickable() && block.getPower() == 15) {
                    val vectorFromIndex = chunk.getVectorFromIndex(i)
                    val position = Vector(
                        vectorFromIndex.x + x * 16,
                        vectorFromIndex.y + section.y * 16,
                        vectorFromIndex.z + z * 16
                    )
                    tickableBlocks.join(world, position)
                }
            }
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
        segment: SegmentBinaryTag,
        binaryTagDriver: BinaryTagDriver,
        chunk: Chunk,
        metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry
    ) {
        try {
            segment.writeCompound(
                chunkId(chunk),
                binaryTagDriver,
                this.serialize(chunk, metadataKeyValueBufferRegistry)
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
        segment: SegmentBinaryTag,
        binaryTagDriver: BinaryTagDriver,
        world: World,
        chunkX: Int,
        chunkZ: Int,
        metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry
    ): Chunk {
        val chunkId = chunkId(chunkX, chunkZ)
        val compoundBinaryTag = segment.readCompound(chunkId, binaryTagDriver)
        return deserialize(compoundBinaryTag, world, metadataKeyValueBufferRegistry)
    }

}