package eu.mshade.enderchest.listener;

import eu.mshade.enderframe.EnderFrameSession;
import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.event.EntitySeeEvent;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

public class EntitySeeHandler implements EventListener<EntitySeeEvent> {

    @Override
    public void onEvent(EntitySeeEvent event, ParameterContainer eventContainer) {
        Entity entity = event.getEntity();
        Player player = event.getPlayer();

        EnderFrameSession enderFrameSession = player.getEnderFrameSessionHandler().getEnderFrameSession();
        enderFrameSession.sendEntity(entity);
        enderFrameSession.sendTeleport(entity, false);
        entity.getViewers().add(player);
    }
}
