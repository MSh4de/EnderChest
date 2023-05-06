package eu.mshade.enderchest.listener.packet;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.entity.metadata.SkinPartEntityMetadata;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.enderframe.packetevent.MinecraftPacketClientSettingsEvent;
import eu.mshade.mwork.event.EventListener;

public class MinecraftPacketClientSettingsListener implements EventListener<MinecraftPacketClientSettingsEvent> {

    @Override
    public void onEvent(MinecraftPacketClientSettingsEvent event) {

        Player player = event.getPlayer();
        MetadataKeyValueBucket metadata = player.getMetadata();
        metadata.setMetadataKeyValue(new SkinPartEntityMetadata(event.getDisplayedSkinParts()));

    }
}
