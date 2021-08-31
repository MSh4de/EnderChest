package eu.mshade.enderchest.listener;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.event.ChunkLookEvent;
import eu.mshade.enderframe.world.ChunkBuffer;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

public class ChunkSeeHandler implements EventListener<ChunkLookEvent> {

    @Override
    public void onEvent(ChunkLookEvent event, ParameterContainer eventContainer) {
        ChunkBuffer chunkBuffer = event.getChunkBuffer();
        Player player = event.getPlayer();


    }
}
