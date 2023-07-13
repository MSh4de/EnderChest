package eu.mshade.enderchest.listener.chunk;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.event.ChunkSeeEvent;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.mwork.event.EventListener;

public class ChunkSeeListener implements EventListener<ChunkSeeEvent> {

    @Override
    public void onEvent(ChunkSeeEvent event) {
        Chunk chunk = event.getChunkBuffer();
        Player player = event.getPlayer();
    }
}
