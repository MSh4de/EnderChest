package eu.mshade.enderchest.listener;

import eu.mshade.enderframe.event.ChunkUnloadEvent;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

public class ChunkUnloadHandler implements EventListener<ChunkUnloadEvent> {

    @Override
    public void onEvent(ChunkUnloadEvent event, ParameterContainer eventContainer) {
        Chunk chunk = event.getChunkBuffer();


    }
}
