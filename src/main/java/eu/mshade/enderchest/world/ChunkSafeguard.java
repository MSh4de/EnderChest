package eu.mshade.enderchest.world;

import eu.mshade.enderframe.world.Chunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ChunkSafeguard implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChunkSafeguard.class);
    protected BlockingQueue<Chunk> chunkBlockingQueue = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        while (true) {
            try {
                Chunk chunk = chunkBlockingQueue.take();
                chunk.getWorld().saveChunk(chunk);
                chunk.getInChunkSafeguard().set(false);
            } catch (InterruptedException e) {
                LOGGER.error("ChunkSafeguard interrupted", e);
            }
        }
    }
    public void addChunk(Chunk chunk) {
        chunk.getInChunkSafeguard().set(true);
        chunkBlockingQueue.add(chunk);
    }

}
