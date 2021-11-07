package eu.mshade.enderchest.listener;

import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.event.EntityUnseeEvent;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

public class EntityUnseeHandler implements EventListener<EntityUnseeEvent> {

    @Override
    public void onEvent(EntityUnseeEvent event, ParameterContainer eventContainer) {
        Entity entity = event.getEntity();
        Player player = event.getPlayer();


    }
}
