package eu.mshade.enderchest.world;

import eu.mshade.enderframe.world.*;
import eu.mshade.mwork.MWork;
import eu.mshade.mwork.binarytag.BinaryTagType;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.entity.ZstdByteArrayBinaryTag;
import eu.mshade.mwork.binarytag.entity.ZstdListBinaryTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class WorldBufferIO {

    private final Logger logger = LoggerFactory.getLogger(WorldBufferIO.class);



    public void writeWorldLevel(WorldLevel worldLevel, File file){
         if (!file.exists()){
             try {
                 file.createNewFile();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();
        compoundBinaryTag.putString("name", worldLevel.getName());
        compoundBinaryTag.putLong("seed", worldLevel.getSeed());
        compoundBinaryTag.putString("levelType", worldLevel.getLevelType().toString());
        compoundBinaryTag.putString("dimension", worldLevel.getDimension().toString());
        compoundBinaryTag.putString("difficulty", worldLevel.getDifficulty().toString());
        MWork.get().getBinaryTagBufferDriver().writeCompoundBinaryTag(compoundBinaryTag, file);
    }

    public WorldLevel readWorldLevel(File file){
            CompoundBinaryTag compoundBinaryTag = MWork.get().getBinaryTagBufferDriver().readCompoundBinaryTag(file);
            String name = compoundBinaryTag.getString("name");
            long seed = compoundBinaryTag.getLong("seed");
            LevelType levelType = LevelType.valueOf(compoundBinaryTag.getString("levelType"));
            Dimension dimension = Dimension.valueOf(compoundBinaryTag.getString("dimension"));
            Difficulty difficulty = Difficulty.valueOf(compoundBinaryTag.getString("difficulty"));
            return new WorldLevel(name, seed, levelType, dimension, difficulty);

    }

    public void writeChunkBuffer(ChunkBuffer chunkBuffer){
        synchronized (chunkBuffer.getFile()){
            CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();
            compoundBinaryTag.putInt("x", chunkBuffer.getX());
            compoundBinaryTag.putInt("z", chunkBuffer.getZ());
            compoundBinaryTag.putBinaryTag("biomes", new ZstdByteArrayBinaryTag(chunkBuffer.getBiomes()));
            ZstdListBinaryTag listBinaryTag = new ZstdListBinaryTag(BinaryTagType.COMPOUND);
            for (SectionBuffer sectionBuffer : chunkBuffer.getSectionBuffers()) {
                if (sectionBuffer != null) {
                    CompoundBinaryTag sectionBufferTag = new CompoundBinaryTag();
                    sectionBufferTag.putInt("y", sectionBuffer.getY());
                    sectionBufferTag.putInt("realBlock", sectionBuffer.getRealBlock());
                    sectionBufferTag.putIntArray("blocks", sectionBuffer.getBlocks());
                    sectionBufferTag.putByteArray("data", sectionBuffer.getData().getRawData());
                    sectionBufferTag.putByteArray("blockLight", sectionBuffer.getBlockLight().getRawData());
                    sectionBufferTag.putByteArray("skyLight", sectionBuffer.getSkyLight().getRawData());
                    listBinaryTag.add(sectionBufferTag);
                }
            }
            compoundBinaryTag.putBinaryTag("sections", listBinaryTag);
            if (!chunkBuffer.getFile().exists()) {
                try {
                    chunkBuffer.getFile().createNewFile();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
            MWork.get().getBinaryTagBufferDriver().writeCompoundBinaryTag(compoundBinaryTag, chunkBuffer.getFile());
        }
    }

    public ChunkBuffer readChunkBuffer(WorldBuffer worldBuffer, File file) {
        synchronized (file){
            CompoundBinaryTag compoundBinaryTag = MWork.get().getBinaryTagBufferDriver().readCompoundBinaryTag(file);
            int x = compoundBinaryTag.getInt("x");
            int z = compoundBinaryTag.getInt("z");
            byte[] biomes = (byte[]) compoundBinaryTag.getBinaryTag("biomes").getValue();
            DefaultChunkBuffer defaultChunkBuffer = new DefaultChunkBuffer(x, z, worldBuffer, file);
            defaultChunkBuffer.setBiomes(biomes);
            ZstdListBinaryTag sections = (ZstdListBinaryTag) compoundBinaryTag.getBinaryTag("sections");
            sections.forEach(binaryTag -> {
                CompoundBinaryTag sectionBufferTag = (CompoundBinaryTag) binaryTag;

                int y = sectionBufferTag.getInt("y");
                int realBlock = sectionBufferTag.getInt("realBlock");
                int[] blocks = sectionBufferTag.getIntArray("blocks");
                byte[] data = sectionBufferTag.getByteArray("data");
                byte[] blockLight = sectionBufferTag.getByteArray("blockLight");
                byte[] skyLight = sectionBufferTag.getByteArray("skyLight");

                DefaultSectionBuffer defaultSectionBuffer = new DefaultSectionBuffer(defaultChunkBuffer, y, realBlock);
                defaultSectionBuffer.setBlocks(blocks);
                defaultSectionBuffer.getData().setRawData(data);
                defaultSectionBuffer.getBlockLight().setRawData(blockLight);
                defaultSectionBuffer.getSkyLight().setRawData(skyLight);
                defaultChunkBuffer.getSectionBuffers()[y] = defaultSectionBuffer;
            });
            return defaultChunkBuffer;
        }
    }

}