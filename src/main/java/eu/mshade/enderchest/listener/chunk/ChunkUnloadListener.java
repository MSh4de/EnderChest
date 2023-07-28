package eu.mshade.enderchest.listener.chunk;

import eu.mshade.enderframe.event.ChunkUnloadEvent;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.mwork.event.EventListener;

public class ChunkUnloadListener implements EventListener<ChunkUnloadEvent> {

    @Override
    public void onEvent(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
    }

}
