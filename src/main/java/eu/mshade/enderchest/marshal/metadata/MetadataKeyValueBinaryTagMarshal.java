package eu.mshade.enderchest.marshal.metadata;

import eu.mshade.enderframe.metadata.MetadataKey;
import eu.mshade.enderframe.metadata.MetadataKeyValue;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.enderframe.metadata.world.WorldMetadataType;
import eu.mshade.enderframe.world.Difficulty;
import eu.mshade.enderframe.world.Dimension;
import eu.mshade.enderframe.world.LevelType;
import eu.mshade.enderframe.world.metadata.*;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.BinaryTagDynamicMarshal;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.entity.LongBinaryTag;
import eu.mshade.mwork.binarytag.entity.StringBinaryTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MetadataKeyValueBinaryTagMarshal implements BinaryTagDynamicMarshal {

    private static Logger LOGGER = LoggerFactory.getLogger(MetadataKeyValueBinaryTagMarshal.class);

    private final Map<String, MetadataKey> metadataKeyByName = new HashMap<>();
    private final Map<MetadataKey, Function<MetadataKeyValue<?>, BinaryTag<?>>> metadataKeyValueToBinaryTag = new HashMap<>();
    private final Map<MetadataKey, Function<BinaryTag<?>, MetadataKeyValue<?>>> binaryTagToMetadataKeyValue = new HashMap<>();

    public MetadataKeyValueBinaryTagMarshal(BinaryTagDriver binaryTagDriver) {
        this.register(WorldMetadataType.NAME, metadataKeyValue -> new StringBinaryTag((String) metadataKeyValue.getMetadataValue()), binaryTag -> new NameWorldMetadata((String) binaryTag.getValue()));
        this.register(WorldMetadataType.SEED, metadataKeyValue -> new LongBinaryTag((Long) metadataKeyValue.getMetadataValue()), binaryTag -> new SeedWorldMetadata((Long) binaryTag.getValue()));
        this.register(WorldMetadataType.DIMENSION, metadataKeyValue -> binaryTagDriver.marshal(metadataKeyValue.getMetadataValue()), binaryTag -> new DimensionWorldMetadata(binaryTagDriver.unMarshal(binaryTag, Dimension.class)));
        this.register(WorldMetadataType.LEVEL_TYPE, metadataKeyValue -> binaryTagDriver.marshal(metadataKeyValue.getMetadataValue()), binaryTag -> new LevelTypeWorldMetadata(binaryTagDriver.unMarshal(binaryTag, LevelType.class)));
        this.register(WorldMetadataType.DIFFICULTY, metadataKeyValue -> binaryTagDriver.marshal(metadataKeyValue.getMetadataValue()), binaryTag -> new DifficultyWorldMetadata(binaryTagDriver.unMarshal(binaryTag, Difficulty.class)));
        LOGGER.info("Register {} metadataKeyValues", metadataKeyByName.size());
    }

    public CompoundBinaryTag serialize(MetadataKeyValueBucket metadataKeyValueBucket){
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();

        for (MetadataKeyValue<?> metadataKeyValue : metadataKeyValueBucket.getMetadataKeyValues()) {
            compoundBinaryTag.putBinaryTag(metadataKeyValue.getMetadataKey().getName(), metadataKeyValueToBinaryTag.get(metadataKeyValue.getMetadataKey()).apply(metadataKeyValue));
        }

        return compoundBinaryTag;
    }

    public MetadataKeyValueBucket deserialize(CompoundBinaryTag compoundBinaryTag){
        MetadataKeyValueBucket metadataKeyValueBucket = new MetadataKeyValueBucket();

        compoundBinaryTag.getValue().forEach((s, binaryTag) -> {
            metadataKeyValueBucket.setMetadataKeyValue(binaryTagToMetadataKeyValue.get(metadataKeyByName.get(s)).apply(binaryTag));
        });

        return metadataKeyValueBucket;
    }

    public <K extends MetadataKey> void register(K metadataKey, Function<MetadataKeyValue<?>, BinaryTag<?>> metadataKeyValueToBinaryTag, Function<BinaryTag<?>, MetadataKeyValue<?>> binaryTagToMetadataKeyValue){
        this.metadataKeyByName.put(metadataKey.getName(), metadataKey);
        this.metadataKeyValueToBinaryTag.put(metadataKey, metadataKeyValueToBinaryTag);
        this.binaryTagToMetadataKeyValue.put(metadataKey, binaryTagToMetadataKeyValue);
        LOGGER.debug("Register "+metadataKey);
    }

}
