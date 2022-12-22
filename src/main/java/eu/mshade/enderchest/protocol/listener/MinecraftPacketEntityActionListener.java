package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.entity.metadata.SprintingEntityMetadata;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.enderframe.packetevent.MinecraftPacketEntityActionEvent;
import eu.mshade.mwork.event.EventListener;

public class MinecraftPacketEntityActionListener implements EventListener<MinecraftPacketEntityActionEvent> {

    @Override
    public void onEvent(MinecraftPacketEntityActionEvent event) {
        Player player = event.getPlayer();
        MetadataKeyValueBucket metadataKeyValueBucket = player.getMetadataKeyValueBucket();


        switch (event.getActionType()){
            case START_SNEAKING:
                player.setSneaking(true);
                break;
            case STOP_SNEAKING:
                player.setSneaking(false);
                break;
            case START_SPRINTING:
                metadataKeyValueBucket.setMetadataKeyValue(new SprintingEntityMetadata(true));
                break;
            case STOP_SPRINTING:
                metadataKeyValueBucket.setMetadataKeyValue(new SprintingEntityMetadata(false));
                break;
            default: break;
        }

        /*
        switch (event.getActionType()){
            case START_SNEAKING:
                player.setSneaking(true);
                break;
            case STOP_SNEAKING:
                player.setSneaking(false);
                break;
            case START_SPRINTING:
                player.setSprinting(true);
                break;
            case STOP_SPRINTING:
                player.setSprinting(false);
                break;
            default: break;
        }
        player.getViewers().forEach(target -> target.getEnderFrameSessionHandler().getEnderFrameSession().sendMetadata(player, EntityMetadataType.ENTITY_PROPERTIES));

         */
    }
}
