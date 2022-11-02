package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.entity.metadata.FlyingEntityMetadata;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.enderframe.packetevent.PacketToggleFlyingEvent;
import eu.mshade.mwork.event.EventListener;

public class PacketToggleFlyingListener implements EventListener<PacketToggleFlyingEvent> {

    @Override
    public void onEvent(PacketToggleFlyingEvent event) {
        Player player = event.getPlayer();

        MetadataKeyValueBucket metadataKeyValueBucket = player.getMetadataKeyValueBucket();
        metadataKeyValueBucket.setMetadataKeyValue(new FlyingEntityMetadata(event.isFlying()));
    }
}
