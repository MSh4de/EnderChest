package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.entity.metadata.FlyingEntityMetadata;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.enderframe.packetevent.MinecraftPacketToggleFlyingEvent;
import eu.mshade.mwork.event.EventListener;

public class MinecraftPacketToggleFlyingListener implements EventListener<MinecraftPacketToggleFlyingEvent> {

    @Override
    public void onEvent(MinecraftPacketToggleFlyingEvent event) {
        Player player = event.getPlayer();

        MetadataKeyValueBucket metadataKeyValueBucket = player.getMetadataKeyValueBucket();
        metadataKeyValueBucket.setMetadataKeyValue(new FlyingEntityMetadata(event.isFlying()));
    }
}
