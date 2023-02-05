package eu.mshade.enderchest.listener;

import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.event.EntityMoveEvent;
import eu.mshade.mwork.event.EventListener;

public class EntityMoveListener implements EventListener<EntityMoveEvent> {

    @Override
    public void onEvent(EntityMoveEvent event) {
        Entity entity = event.getEntity();


    }
}
