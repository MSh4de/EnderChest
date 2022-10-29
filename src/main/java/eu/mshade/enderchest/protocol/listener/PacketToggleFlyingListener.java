package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.entity.metadata.FlyingEntityMetadata;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.enderframe.metadata.entity.EntityMetadataKey;
import eu.mshade.enderframe.packetevent.PacketToggleFlyingEvent;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import io.netty.channel.Channel;

public class PacketToggleFlyingListener implements EventListener<PacketToggleFlyingEvent> {

    @Override
    public void onEvent(PacketToggleFlyingEvent event) {
        Player player = event.getPlayer();

        MetadataKeyValueBucket metadataKeyValueBucket = player.getMetadataKeyValueBucket();
        metadataKeyValueBucket.setMetadataKeyValue(new FlyingEntityMetadata(event.isFlying()));
    }
}
