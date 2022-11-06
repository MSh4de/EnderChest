package eu.mshade.enderchest.listener;

import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.event.EntityTeleportEvent;
import eu.mshade.mwork.event.EventListener;

public class EntityTeleportHandler implements EventListener<EntityTeleportEvent> {

    @Override
    public void onEvent(EntityTeleportEvent event) {
        Entity entity = event.getEntity();

    }
}
