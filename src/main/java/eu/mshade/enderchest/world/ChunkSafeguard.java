package eu.mshade.enderchest.world;

import eu.mshade.enderframe.world.ChunkStatus;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.enderframe.world.chunk.ChunkStateStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

public class ChunkSafeguard extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChunkSafeguard.class);
    protected BlockingQueue<Chunk> chunkBlockingQueue = new LinkedBlockingQueue<>();
    protected CompletableFuture<Boolean> waitingFinishLastChunk = new CompletableFuture<>();
    protected boolean running = true;

    public ChunkSafeguard() {
        super("ChunkSafeguard");
    }

    @Override
    public void run() {
        while (running) {
            Chunk chunk = chunkBlockingQueue.poll();
            if (chunk != null) {
                chunk.getWorld().saveChunk(chunk);
                ChunkStateStore chunkStateStore = chunk.getChunkStateStore();
                chunkStateStore.leaveChunkSafeguard();
                if (chunkStateStore.getChunkStatus() == ChunkStatus.PREPARE_TO_UNLOAD && running)
                    chunkStateStore.finishWrite();
            }
        }
        waitingFinishLastChunk.complete(true);
    }

    public void stopSafeguard() {
        running = false;
        try {
            boolean unused = waitingFinishLastChunk.get(5, TimeUnit.SECONDS);
            LOGGER.info("ChunkSafeguard is finished");
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            LOGGER.error("Impossible to waiting last chunk", e);
        }
    }

    public void addChunk(Chunk chunk) {
        chunk.getChunkStateStore().joinChunkSafeguard();
        chunkBlockingQueue.add(chunk);
    }


}
