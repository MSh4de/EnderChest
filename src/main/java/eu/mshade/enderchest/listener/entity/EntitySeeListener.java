package eu.mshade.enderchest.listener.entity;

import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.event.EntitySeeEvent;
import eu.mshade.mwork.event.EventListener;

public class EntitySeeListener implements EventListener<EntitySeeEvent> {

    @Override
    public void onEvent(EntitySeeEvent event) {
        Entity entity = event.getEntity();
        Player player = event.getPlayer();


    }
}
