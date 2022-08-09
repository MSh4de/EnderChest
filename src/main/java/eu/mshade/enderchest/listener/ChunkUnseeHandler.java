package eu.mshade.enderchest.listener;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.event.ChunkUnseeEvent;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

public class ChunkUnseeHandler implements EventListener<ChunkUnseeEvent> {

    @Override
    public void onEvent(ChunkUnseeEvent event, ParameterContainer eventContainer) {
        Chunk chunk = event.getChunkBuffer();
        Player player = event.getPlayer();

    }
}
