package eu.mshade.enderchest.listener.packet;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.entity.metadata.CrouchedEntityMetadata;
import eu.mshade.enderframe.entity.metadata.SprintingEntityMetadata;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.enderframe.packetevent.MinecraftPacketEntityActionEvent;
import eu.mshade.mwork.event.EventListener;

public class MinecraftPacketEntityActionListener implements EventListener<MinecraftPacketEntityActionEvent> {

    @Override
    public void onEvent(MinecraftPacketEntityActionEvent event) {
        Player player = event.getPlayer();
        MetadataKeyValueBucket metadata = player.getMetadata();


        switch (event.getActionType()){
            case START_SNEAKING:
                metadata.setMetadataKeyValue(new CrouchedEntityMetadata(true));
                break;
            case STOP_SNEAKING:
                metadata.setMetadataKeyValue(new CrouchedEntityMetadata(false));
                break;
            case START_SPRINTING:
                metadata.setMetadataKeyValue(new SprintingEntityMetadata(true));
                break;
            case STOP_SPRINTING:
                metadata.setMetadataKeyValue(new SprintingEntityMetadata(false));
                break;
            default: break;
        }

    }
}
