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


    }
}