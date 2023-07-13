package eu.mshade.enderchest.listener.chunk;

import eu.mshade.enderchest.axolotl.AxololtConnection;
import eu.mshade.enderframe.event.ChunkLoadEvent;
import eu.mshade.mwork.event.EventListener;

import java.util.concurrent.CompletableFuture;

public class ChunkLoadListener implements EventListener<ChunkLoadEvent> {

    @Override
    public void onEvent(ChunkLoadEvent event) {
        CompletableFuture.runAsync(() -> {
            AxololtConnection.INSTANCE.send(axolotlSession -> axolotlSession.sendChunk(event.getChunk().join()));
        });

    }
}
