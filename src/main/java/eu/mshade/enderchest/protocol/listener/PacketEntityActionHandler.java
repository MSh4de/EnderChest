package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.EnderFrameSession;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.packetevent.PacketEntityActionEvent;
import eu.mshade.enderframe.metadata.MetadataMeaning;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

public class PacketEntityActionHandler implements EventListener<PacketEntityActionEvent> {

    @Override
    public void onEvent(PacketEntityActionEvent event, ParameterContainer eventContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = eventContainer.getContainer(EnderFrameSessionHandler.class);
        EnderFrameSession enderFrameSession = enderFrameSessionHandler.getEnderFrameSession();

        Player player = enderFrameSession.getPlayer();

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
        player.getViewers().forEach(target -> target.getEnderFrameSessionHandler().getEnderFrameSession().sendMetadata(player, MetadataMeaning.ENTITY_PROPERTIES));
    }
}
