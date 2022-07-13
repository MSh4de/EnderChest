package eu.mshade.enderchest.listener;

import eu.mshade.enderframe.event.ChunkLoadEvent;
import eu.mshade.enderframe.world.Chunk;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

public class ChunkLoadHandler implements EventListener<ChunkLoadEvent> {

    @Override
    public void onEvent(ChunkLoadEvent event, ParameterContainer eventContainer) {
        Chunk chunk = event.getChunkBuffer();


    }
}
