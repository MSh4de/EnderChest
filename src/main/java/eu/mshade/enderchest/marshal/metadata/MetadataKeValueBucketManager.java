package eu.mshade.enderchest.marshal.metadata;

import eu.mshade.enderframe.metadata.MetadataKey;
import eu.mshade.enderframe.metadata.MetadataKeyValue;
import eu.mshade.mwork.binarytag.BinaryTag;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MetadataKeValueBucketManager {

    private final Map<String, MetadataKey> metadataKeyByName = new HashMap<>();
    private final Map<MetadataKey, Function<MetadataKeyValue<?,?>, BinaryTag<?>>> metadataKeyValueToBinaryTag = new HashMap<>();
    private final Map<MetadataKey, Function<BinaryTag<?>, MetadataKeyValue<?, ?>>> binaryTagToMetadataKeyValue = new HashMap<>();

    public <K extends MetadataKey> void register(K metadataKey, Function<MetadataKeyValue<?, ?>, BinaryTag<?>> metadataKeyValueToBinaryTag, Function<BinaryTag<?>, MetadataKeyValue<?, ?>> binaryTagToMetadataKeyValue){
        this.metadataKeyByName.put(metadataKey.getName(), metadataKey);
        this.metadataKeyValueToBinaryTag.put(metadataKey, metadataKeyValueToBinaryTag);
        this.binaryTagToMetadataKeyValue.put(metadataKey, binaryTagToMetadataKeyValue);
    }

    public MetadataKeyValue<?, ?> getMetadataKeyValue(String key, BinaryTag<?> binaryTag){
        MetadataKey metadataKey = metadataKeyByName.get(key);
        return binaryTagToMetadataKeyValue.get(metadataKey).apply(binaryTag);
    }

    public BinaryTag<?> getBinaryTag(MetadataKeyValue<?, ?> metadataKeyValue){
        return this.metadataKeyValueToBinaryTag.get(metadataKeyValue.getMetadataKey()).apply(metadataKeyValue);
    }

}
