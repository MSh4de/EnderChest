package eu.mshade.enderchest.listener;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.event.ChunkSeeEvent;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

public class ChunkSeeHandler implements EventListener<ChunkSeeEvent> {

    @Override
    public void onEvent(ChunkSeeEvent event) {
        Chunk chunk = event.getChunkBuffer();
        Player player = event.getPlayer();
    }
}
