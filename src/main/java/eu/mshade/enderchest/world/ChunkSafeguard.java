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
    protected boolean running = true;
    protected CompletableFuture<Void> waitingFinishLastChunk = new CompletableFuture<>();

    public ChunkSafeguard() {
        super("ChunkSafeguard");
    }

    @Override
    public void run() {
        while (running) {
            try {
                Chunk chunk = chunkBlockingQueue.take();
                chunk.getWorld().saveChunk(chunk);
                ChunkStateStore chunkStateStore = chunk.getChunkStateStore();
                chunkStateStore.leaveChunkSafeguard();
                if (chunkStateStore.getChunkStatus() == ChunkStatus.PREPARE_TO_UNLOAD) chunkStateStore.finishWrite();
                if (!running) {
                    waitingFinishLastChunk.complete(null);
                }
            }catch (Exception e) {
                LOGGER.error("Impossible to take chunk", e);
            }
        }

    }

    public void stopSafeguard() {
        running = false;
        LOGGER.info("ChunkSafeguard is finished");
        try {
            waitingFinishLastChunk.get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Impossible to waiting last chunk", e);
        }
    }

    public void addChunk(Chunk chunk) {
        chunk.getChunkStateStore().joinChunkSafeguard();
        chunkBlockingQueue.add(chunk);
    }


}
