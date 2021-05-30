package eu.mshade.enderchest.world;

import eu.mshade.KeyValuePair;
import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.EnderFrameSession;
import eu.mshade.enderframe.world.ChunkBuffer;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.WorldBuffer;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class WatchDogChunk {

    private final Queue<ChunkBuffer> watchDogChunk = new ConcurrentLinkedQueue<>();
    private static Logger logger = LoggerFactory.getLogger(WatchDogChunk.class);

    public WatchDogChunk(DedicatedEnderChest dedicatedEnderChest) {
        logger.info("Running WatchDogChunk");
        dedicatedEnderChest.getEventLoopGroup().scheduleAtFixedRate(() -> {
            for (ChunkBuffer chunkBuffer : watchDogChunk) {
                long delay = System.currentTimeMillis() - chunkBuffer.getHealth().get();
                if (delay > 1000 && chunkBuffer.getViewers().isEmpty()){
                    WorldBuffer worldBuffer = chunkBuffer.getWorldBuffer();
                    worldBuffer.flushChunkBuffer(chunkBuffer);
                    watchDogChunk.remove(chunkBuffer);
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    public void addChunkBuffer(ChunkBuffer chunkBuffer){
        this.watchDogChunk.add(chunkBuffer);
    }

}
