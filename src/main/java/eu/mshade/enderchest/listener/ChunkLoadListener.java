package eu.mshade.enderchest.listener;

import eu.mshade.enderchest.axolotl.AxololtConnection;
import eu.mshade.mwork.event.EventListener;

import java.util.concurrent.CompletableFuture;

public class ChunkLoadListener implements EventListener<ChunkLoadEvent> {

    @Override
    public void onEvent(ChunkLoadEvent event) {
        CompletableFuture.runAsync(() -> {
            AxololtConnection.INSTANCE.send(axolotlSession -> axolotlSession.sendChunk(event.chunk.join()));
        });

    }
}
