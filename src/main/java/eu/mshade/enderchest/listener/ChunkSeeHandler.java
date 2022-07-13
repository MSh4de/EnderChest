package eu.mshade.enderchest.listener;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.event.ChunkSeeEvent;
import eu.mshade.enderframe.world.Chunk;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

public class ChunkSeeHandler implements EventListener<ChunkSeeEvent> {

    @Override
    public void onEvent(ChunkSeeEvent event, ParameterContainer eventContainer) {
        Chunk chunk = event.getChunkBuffer();
        Player player = event.getPlayer();
    }
}
