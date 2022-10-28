package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.entity.metadata.SprintingEntityMetadata;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.enderframe.metadata.entity.EntityMetadataKey;
import eu.mshade.enderframe.packetevent.PacketEntityActionEvent;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import io.netty.channel.Channel;

public class PacketEntityActionHandler implements EventListener<PacketEntityActionEvent> {

    @Override
    public void onEvent(PacketEntityActionEvent event, ParameterContainer parameterContainer) {
        Channel channel = parameterContainer.getContainer(Channel.class);
        Player player = ProtocolPipeline.get().getPlayer(channel);
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
