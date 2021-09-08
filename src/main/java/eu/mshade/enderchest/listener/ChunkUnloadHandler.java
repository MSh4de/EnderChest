package eu.mshade.enderchest.listener;

import eu.mshade.enderframe.event.ChunkUnloadEvent;
import eu.mshade.enderframe.world.ChunkBuffer;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

public class ChunkUnloadHandler implements EventListener<ChunkUnloadEvent> {

    @Override
    public void onEvent(ChunkUnloadEvent event, ParameterContainer eventContainer) {
        ChunkBuffer chunkBuffer = event.getChunkBuffer();


    }
}
