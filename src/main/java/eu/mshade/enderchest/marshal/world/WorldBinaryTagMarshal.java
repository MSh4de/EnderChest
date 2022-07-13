package eu.mshade.enderchest.marshal.world;

import eu.mshade.enderchest.marshal.metadata.MetadataKeyValueBinaryTagMarshal;
import eu.mshade.enderchest.world.DefaultWorld;
import eu.mshade.enderchest.world.WorldManager;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.enderframe.metadata.world.WorldMetadataType;
import eu.mshade.enderframe.world.World;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.BinaryTagDynamicMarshal;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WorldBinaryTagMarshal implements BinaryTagDynamicMarshal {

    public CompoundBinaryTag serialize(BinaryTagDriver binaryTagDriver, World world){
        MetadataKeyValueBinaryTagMarshal metadataKeyValueBinaryTagMarshal = binaryTagDriver.getDynamicMarshal(MetadataKeyValueBinaryTagMarshal.class);
        return metadataKeyValueBinaryTagMarshal.serialize(WorldMetadataType.class, world.getMetadataKeyValueBucket());
    }

    public World deserialize(BinaryTagDriver binaryTagDriver, BinaryTag<?> binaryTag, WorldManager worldManager, File worldFolder){
        MetadataKeyValueBinaryTagMarshal metadataKeyValueBinaryTagMarshal = binaryTagDriver.getDynamicMarshal(MetadataKeyValueBinaryTagMarshal.class);
        MetadataKeyValueBucket metadataKeyValueBucket = metadataKeyValueBinaryTagMarshal.deserialize(WorldMetadataType.class, (CompoundBinaryTag) binaryTag);
        return new DefaultWorld(worldManager, worldFolder, metadataKeyValueBucket);
    }

    public void write(BinaryTagDriver binaryTagDriver, World world){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(world.getWorldFolder(), "level.dat"));
            binaryTagDriver.writeCompoundBinaryTag(serialize(binaryTagDriver, world), fileOutputStream);
            fileOutputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public World read(BinaryTagDriver binaryTagDriver, File file, WorldManager worldManager){
        CompoundBinaryTag compoundBinaryTag = binaryTagDriver.readCompoundBinaryTag(new File(file, "level.dat"));
        return deserialize(binaryTagDriver, compoundBinaryTag, worldManager, file);
    }

}
