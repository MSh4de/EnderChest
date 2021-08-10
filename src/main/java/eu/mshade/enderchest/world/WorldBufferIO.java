package eu.mshade.enderchest.world;

import eu.mshade.enderchest.marshals.DefaultChunkMarshal;
import eu.mshade.enderframe.world.*;
import eu.mshade.mwork.MWork;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.DefaultBinaryTagMarshal;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class WorldBufferIO {

    private final Logger logger = LoggerFactory.getLogger(WorldBufferIO.class);


    public void writeWorldLevel(WorldLevel worldLevel, File file) {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();
        compoundBinaryTag.putString("name", worldLevel.getName());
        compoundBinaryTag.putLong("seed", worldLevel.getSeed());
        compoundBinaryTag.putString("levelType", worldLevel.getLevelType().toString());
        compoundBinaryTag.putString("dimension", worldLevel.getDimension().toString());
        compoundBinaryTag.putString("difficulty", worldLevel.getDifficulty().toString());
        try(FileOutputStream fileOutputStream = new FileOutputStream(file)){
            MWork.get().getBinaryTagBufferDriver().writeCompoundBinaryTag(compoundBinaryTag, fileOutputStream);
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    public WorldLevel readWorldLevel(File file) {
        CompoundBinaryTag compoundBinaryTag = MWork.get().getBinaryTagBufferDriver().readCompoundBinaryTag(file);
        String name = compoundBinaryTag.getString("name");
        long seed = compoundBinaryTag.getLong("seed");
        LevelType levelType = LevelType.valueOf(compoundBinaryTag.getString("levelType"));
        Dimension dimension = Dimension.valueOf(compoundBinaryTag.getString("dimension"));
        Difficulty difficulty = Difficulty.valueOf(compoundBinaryTag.getString("difficulty"));
        return new WorldLevel(name, seed, levelType, dimension, difficulty);

    }

    public void writeChunkBuffer(ChunkBuffer chunkBuffer) {
        if (chunkBuffer.hasChange()) {
            synchronized (chunkBuffer.getFile()) {
                try {
                    CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) MWork.get().getBinaryTagMarshal().marshal(chunkBuffer);
                    FileOutputStream fileOutputStream = new FileOutputStream(chunkBuffer.getFile());
                    MWork.get().getBinaryTagBufferDriver().writeCompoundBinaryTag(compoundBinaryTag, fileOutputStream);
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }
    }

    public ChunkBuffer readChunkBuffer(WorldBuffer worldBuffer, File file) {
        synchronized (file) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                CompoundBinaryTag compoundBinaryTag = MWork.get().getBinaryTagBufferDriver().readCompoundBinaryTag(fileInputStream);
                ParameterContainer parameterContainer = new ParameterContainer();
                parameterContainer.putContainer(file);
                parameterContainer.putContainer(worldBuffer);
                return MWork.get().getBinaryTagMarshal().unMarshal(compoundBinaryTag, ChunkBuffer.class, parameterContainer);

            } catch (Exception e) {e.printStackTrace();}
        }
        return null;
    }

}
