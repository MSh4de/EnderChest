package eu.mshade.enderchest.listener;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.event.ChunkUnseeEvent;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.mwork.event.EventListener;

public class ChunkUnseeListener implements EventListener<ChunkUnseeEvent> {

    @Override
    public void onEvent(ChunkUnseeEvent event) {
        Chunk chunk = event.getChunkBuffer();
        Player player = event.getPlayer();

    }
}
