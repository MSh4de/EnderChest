package eu.mshade.enderchest.world;

import eu.mshade.enderframe.world.ChunkStatus;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.enderframe.world.chunk.ChunkStateStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ChunkSafeguard extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChunkSafeguard.class);
    protected Queue<Chunk> chunkBlockingQueue = new ConcurrentLinkedQueue<>();

    public ChunkSafeguard() {
        super("ChunkSafeguard");
    }

    @Override
    public void run() {
        while (true) {
            Chunk chunk = chunkBlockingQueue.poll();
            if (chunk == null) {
                continue;
            }
            chunk.getWorld().saveChunk(chunk);
            ChunkStateStore chunkStateStore = chunk.getChunkStateStore();
            chunkStateStore.leaveChunkSafeguard();
            if (chunkStateStore.getChunkStatus() == ChunkStatus.PREPARE_TO_UNLOAD) chunkStateStore.finishWrite();
        }

    }
    public void addChunk(Chunk chunk) {
        chunk.getChunkStateStore().joinChunkSafeguard();
        chunkBlockingQueue.add(chunk);
    }


}
