package eu.mshade.enderchest.marshals.world;

import eu.mshade.enderchest.world.DefaultChunkBuffer;
import eu.mshade.enderchest.world.DefaultWorldBuffer;
import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.EntityType;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.world.ChunkBuffer;
import eu.mshade.enderframe.world.SectionBuffer;
import eu.mshade.enderframe.world.WorldBuffer;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagType;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.entity.ZstdByteArrayBinaryTag;
import eu.mshade.mwork.binarytag.entity.ZstdListBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshalBuffer;

import java.io.File;
import java.lang.reflect.Type;

public class DefaultChunkMarshal implements BinaryTagMarshalBuffer<ChunkBuffer> {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, ChunkBuffer chunkBuffer, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();

        compoundBinaryTag.putInt("x", chunkBuffer.getX());
        compoundBinaryTag.putInt("z", chunkBuffer.getZ());
        compoundBinaryTag.putBinaryTag("biomes", new ZstdByteArrayBinaryTag(chunkBuffer.getBiomes()));

        ZstdListBinaryTag listBinaryTagSections = new ZstdListBinaryTag(BinaryTagType.COMPOUND);
        ZstdListBinaryTag listBinaryTagEntities = new ZstdListBinaryTag(BinaryTagType.COMPOUND);

        for (SectionBuffer sectionBuffer : chunkBuffer.getSectionBuffers()) {
            if (sectionBuffer != null) listBinaryTagSections.add(binaryTagMarshal.marshal(sectionBuffer));
        }

        for(Entity entity : chunkBuffer.getEntities()) {
            listBinaryTagEntities.add(binaryTagMarshal.marshal(entity));
        }

        compoundBinaryTag.putBinaryTag("sections", listBinaryTagSections);
        compoundBinaryTag.putBinaryTag("entities", listBinaryTagEntities);
        return compoundBinaryTag;
    }

    @Override
    public ChunkBuffer deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;

        File file = parameterContainer.getContainer(File.class);
        WorldBuffer worldBuffer = parameterContainer.getContainer(DefaultWorldBuffer.class);

        int x = compoundBinaryTag.getInt("x");

        int z = compoundBinaryTag.getInt("z");

        byte[] biome = compoundBinaryTag.getByteArray("biomes");
        ZstdListBinaryTag sectionBinaryTags = (ZstdListBinaryTag) compoundBinaryTag.getBinaryTag("sections");
        ZstdListBinaryTag entityBinaryTags = (ZstdListBinaryTag) compoundBinaryTag.getBinaryTag("entities");

        ChunkBuffer chunkBuffer = new DefaultChunkBuffer(x, z, file, false, worldBuffer, biome);
        SectionBuffer[] sectionBuffers = chunkBuffer.getSectionBuffers();

        sectionBinaryTags.forEach(sectionBinaryTag -> {
            SectionBuffer sectionBuffer = binaryTagMarshal.unMarshal(sectionBinaryTag, SectionBuffer.class, parameterContainer.putContainer(chunkBuffer));
            sectionBuffers[sectionBuffer.getY()] = sectionBuffer;
        });

        entityBinaryTags.forEach(entityBinaryTag ->{
            System.out.println(entityBinaryTags.size());
            CompoundBinaryTag compoundBinaryTagEntity = (CompoundBinaryTag)entityBinaryTag;
            EntityType entityType = EntityType.getEntityTypeByName(compoundBinaryTagEntity.getString("entityType"));
            chunkBuffer.addEntity(binaryTagMarshal.unMarshal(entityBinaryTag, entityType.getClazz(), parameterContainer));
        });
        return chunkBuffer;
    }
}
