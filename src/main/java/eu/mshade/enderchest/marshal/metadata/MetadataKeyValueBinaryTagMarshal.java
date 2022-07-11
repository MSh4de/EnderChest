package eu.mshade.enderchest.marshal.metadata;

import eu.mshade.enderframe.metadata.MetadataKey;
import eu.mshade.enderframe.metadata.MetadataKeyValue;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.mwork.binarytag.BinaryTagDynamicMarshal;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;

import java.util.HashMap;
import java.util.Map;

public class MetadataKeyValueBinaryTagMarshal implements BinaryTagDynamicMarshal {

    private final Map<Class<? extends MetadataKey>, MetadataKeValueBucketManager> metadataKeValueBucketManagerByMetadataKey = new HashMap<>();

    public <T extends MetadataKey> CompoundBinaryTag serialize(Class<T> type, MetadataKeyValueBucket<T> metadataKeyValueBucket){
        MetadataKeValueBucketManager metadataKeValueBucketManager = this.metadataKeValueBucketManagerByMetadataKey.get(type);
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();

        for (MetadataKeyValue<?, ?> metadataKeyValue : metadataKeyValueBucket.getMetadataKeyValues()) {
            compoundBinaryTag.putBinaryTag(metadataKeyValue.getMetadataKey().getName(), metadataKeValueBucketManager.getBinaryTag(metadataKeyValue));
        }

        return compoundBinaryTag;
    }

    public <K extends MetadataKey> MetadataKeyValueBucket<K> deserialize(Class<K> type, CompoundBinaryTag compoundBinaryTag){
        MetadataKeValueBucketManager metadataKeValueBucketManager = this.metadataKeValueBucketManagerByMetadataKey.get(type);
        MetadataKeyValueBucket<K> metadataKeyValueBucket = new MetadataKeyValueBucket<>();

        compoundBinaryTag.getValue().forEach((s, binaryTag) -> {
            metadataKeyValueBucket.setMetadataKeyValue((MetadataKeyValue<K, ?>) metadataKeValueBucketManager.getMetadataKeyValue(s, binaryTag));
        });

        return metadataKeyValueBucket;
    }

    public void registerMetadataKeValueBucketManager(Class<? extends MetadataKey> metadataKey, MetadataKeValueBucketManager metadataKeValueBucketManager){
        this.metadataKeValueBucketManagerByMetadataKey.put(metadataKey, metadataKeValueBucketManager);
    }
}
