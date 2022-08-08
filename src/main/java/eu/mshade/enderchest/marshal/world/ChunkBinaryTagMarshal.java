package eu.mshade.enderchest.marshal.world;

import eu.mshade.enderchest.world.DefaultChunk;
import eu.mshade.enderframe.world.Chunk;
import eu.mshade.enderframe.world.Section;
import eu.mshade.enderframe.world.World;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.BinaryTagDynamicMarshal;
import eu.mshade.mwork.binarytag.BinaryTagType;
import eu.mshade.mwork.binarytag.entity.ByteArrayBinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.entity.ListBinaryTag;
import eu.mshade.mwork.binarytag.entity.ZstdCompoundBinaryTag;
import eu.mshade.mwork.binarytag.poet.BinaryTagPoet;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ChunkBinaryTagMarshal implements BinaryTagDynamicMarshal {


    public CompoundBinaryTag serialize(BinaryTagDriver binaryTagDriver, Chunk chunk) {


        CompoundBinaryTag compoundBinaryTag = new ZstdCompoundBinaryTag();

        compoundBinaryTag.putInt("x", chunk.getX());
        compoundBinaryTag.putInt("z", chunk.getZ());
        compoundBinaryTag.putBinaryTag("biomes", new ByteArrayBinaryTag(chunk.getBiomes()));

        ListBinaryTag listBinaryTagSections = new ListBinaryTag(BinaryTagType.COMPOUND);
        ListBinaryTag listBinaryTagEntities = new ListBinaryTag(BinaryTagType.COMPOUND);

        SectionBinaryTagMarshal sectionBinaryTagMarshal = binaryTagDriver.getDynamicMarshal(SectionBinaryTagMarshal.class);

        for (Section section : chunk.getSections()) {
            if (section != null) listBinaryTagSections.add(sectionBinaryTagMarshal.serialize(section));
        }

        /*
        for(Entity entity : chunk.getEntities()) {
            listBinaryTagEntities.add(binaryTagDriver.marshal(entity));
        }

         */

        compoundBinaryTag.putBinaryTag("sections", listBinaryTagSections);
        compoundBinaryTag.putBinaryTag("entities", listBinaryTagEntities);
        return compoundBinaryTag;
    }

    public Chunk deserialize(BinaryTagDriver binaryTagDriver, BinaryTag<?> binaryTag, World world) {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;
        SectionBinaryTagMarshal sectionBinaryTagMarshal = binaryTagDriver.getDynamicMarshal(SectionBinaryTagMarshal.class);

        int x = compoundBinaryTag.getInt("x");

        int z = compoundBinaryTag.getInt("z");

        byte[] biome = compoundBinaryTag.getByteArray("biomes");
        ListBinaryTag sectionBinaryTags = (ListBinaryTag) compoundBinaryTag.getBinaryTag("sections");
        ListBinaryTag entityBinaryTags = (ListBinaryTag) compoundBinaryTag.getBinaryTag("entities");

        Chunk chunk = new DefaultChunk(x, z, world, biome);
        Section[] sections = chunk.getSections();

        sectionBinaryTags.forEach(sectionBinaryTag -> {
            Section section = sectionBinaryTagMarshal.deserialize(sectionBinaryTag, chunk);
            sections[section.getY()] = section;
        });

        /*
        entityBinaryTags.forEach(entityBinaryTag ->{
            CompoundBinaryTag compoundBinaryTagEntity = (CompoundBinaryTag)entityBinaryTag;
            EntityType entityType = EntityType.getEntityTypeByName(compoundBinaryTagEntity.getString("entityType"));
            chunk.addEntity(binaryTagDriver.unMarshal(entityBinaryTag, entityType.getClazz()));
        });

         */
        return chunk;
    }

    private String regionId(Chunk chunk){
        return regionId(chunk.getX(), chunk.getZ());
    }

    private String regionId(int chunkX, int chunkZ){
        return (chunkX >> 5) +","+(chunkZ >> 5);
    }

    private String chunkId(Chunk chunk){
        return chunk.getX()+","+chunk.getZ();
    }
    private String chunkId(int chunkX, int chunkZ){
        return chunkX+","+chunkZ;
    }


    public void write(BinaryTagDriver binaryTagDriver, BinaryTagPoet binaryTagPoet, Chunk chunk){
        try {
            binaryTagPoet.writeCompoundBinaryTag(chunkId(chunk), this.serialize(binaryTagDriver, chunk));
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public Chunk read(BinaryTagDriver binaryTagDriver, BinaryTagPoet binaryTagPoet, World world, int chunkX, int chunkZ) throws IOException {
        String chunkId = chunkId(chunkX, chunkZ);
        CompoundBinaryTag compoundBinaryTag = binaryTagPoet.readCompoundBinaryTag(chunkId);
        return deserialize(binaryTagDriver, compoundBinaryTag, world);
    }


}
