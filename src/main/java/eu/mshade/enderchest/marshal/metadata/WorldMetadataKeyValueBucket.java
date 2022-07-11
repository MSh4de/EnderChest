package eu.mshade.enderchest.marshal.metadata;

import eu.mshade.enderframe.metadata.world.WorldMetadataType;
import eu.mshade.enderframe.world.Difficulty;
import eu.mshade.enderframe.world.Dimension;
import eu.mshade.enderframe.world.LevelType;
import eu.mshade.enderframe.world.metadata.*;
import eu.mshade.mwork.MWork;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.entity.LongBinaryTag;
import eu.mshade.mwork.binarytag.entity.StringBinaryTag;

public class WorldMetadataKeyValueBucket extends MetadataKeValueBucketManager{


    public WorldMetadataKeyValueBucket() {
        BinaryTagDriver binaryTagMarshal = MWork.get().getBinaryTagDriver();

        this.register(WorldMetadataType.NAME, metadataKeyValue -> new StringBinaryTag((String) metadataKeyValue.getMetadataValue()), binaryTag -> new NameWorldMetadata((String) binaryTag.getValue()));
        this.register(WorldMetadataType.SEED, metadataKeyValue -> new LongBinaryTag((Long) metadataKeyValue.getMetadataValue()), binaryTag -> new SeedWorldMetadata((Long) binaryTag.getValue()));
        this.register(WorldMetadataType.DIMENSION, metadataKeyValue -> binaryTagMarshal.marshal(metadataKeyValue.getMetadataValue()), binaryTag -> new DimensionWorldMetadata(binaryTagMarshal.unMarshal(binaryTag, Dimension.class)));
        this.register(WorldMetadataType.LEVEL_TYPE, metadataKeyValue -> binaryTagMarshal.marshal(metadataKeyValue.getMetadataValue()), binaryTag -> new LevelTypeWorldMetadata(binaryTagMarshal.unMarshal(binaryTag, LevelType.class)));
        this.register(WorldMetadataType.DIFFICULTY, metadataKeyValue -> binaryTagMarshal.marshal(metadataKeyValue.getMetadataValue()), binaryTag -> new DifficultyWorldMetadata(binaryTagMarshal.unMarshal(binaryTag, Difficulty.class)));

    }
}
