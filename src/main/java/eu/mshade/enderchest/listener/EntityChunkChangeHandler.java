package eu.mshade.enderchest.listener;

import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.event.EntityChunkChangeEvent;
import eu.mshade.mwork.event.EventListener;

public class EntityChunkChangeHandler implements EventListener<EntityChunkChangeEvent> {

    @Override
    public void onEvent(EntityChunkChangeEvent event) {
        Entity entity = event.getEntity();


    }
}
