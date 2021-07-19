package eu.mshade.enderchest.world;

import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.world.ChunkBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class WatchDogChunk {

    private final Queue<ChunkBuffer> watchDogChunk = new ConcurrentLinkedQueue<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(WatchDogChunk.class);

    public WatchDogChunk(DedicatedEnderChest dedicatedEnderChest) {
        LOGGER.info("Running WatchDogChunk");
        dedicatedEnderChest.getEventLoopGroup().scheduleAtFixedRate(() -> {
            watchDogChunk.forEach(chunkBuffer -> {
                long delay = System.currentTimeMillis() - chunkBuffer.getHealth().get();
                if (delay > 2000 && chunkBuffer.getViewers().isEmpty()){
                    watchDogChunk.remove(chunkBuffer);
                    chunkBuffer.getWorldBuffer().flushChunkBuffer(chunkBuffer);
                }
            });
        }, 0, 500, TimeUnit.MILLISECONDS);


    }

    public void addChunkBuffer(ChunkBuffer chunkBuffer){
        this.watchDogChunk.add(chunkBuffer);
    }


}
