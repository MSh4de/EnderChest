package eu.mshade.enderchest.listener;

import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.event.EntityChunkChangeEvent;
import eu.mshade.enderframe.event.EntityMoveEvent;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

public class EntityMoveHandler implements EventListener<EntityMoveEvent> {

    @Override
    public void onEvent(EntityMoveEvent event, ParameterContainer eventContainer) {
        Entity entity = event.getEntity();

        System.out.println((long) entity.getViewers().size());
        entity.getViewers()
                .stream()
                .map(Player::getEnderFrameSessionHandler)
                .map(EnderFrameSessionHandler::getEnderFrameSession)
                .forEach(session -> session.moveTo(entity));

        if(!entity.getBeforeLocation().getChunkBuffer().equals(entity.getLocation().getChunkBuffer()))
            EnderFrame.get().getEnderFrameEventBus().publish(new EntityChunkChangeEvent(entity));
    }
}
