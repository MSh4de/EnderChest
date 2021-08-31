package eu.mshade.enderchest.listener;

import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.event.EntityChunkChangeEvent;
import eu.mshade.enderframe.event.EntityTeleportEvent;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

public class EntityTeleportHandler implements EventListener<EntityTeleportEvent> {

    @Override
    public void onEvent(EntityTeleportEvent event, ParameterContainer eventContainer) {
        Entity entity = event.getEntity();
        entity.setUnsafeLocation(event.getLocation());
        entity.getViewers()
                .stream()
                .map(Player::getEnderFrameSessionHandler)
                .map(EnderFrameSessionHandler::getEnderFrameSession)
                .forEach(session -> session.sendTeleport(entity));

        if(!entity.getBeforeLocation().getChunkBuffer().equals(entity.getLocation().getChunkBuffer()))
            EnderFrame.get().getEnderFrameEventBus().publish(new EntityChunkChangeEvent(entity));
    }
}
