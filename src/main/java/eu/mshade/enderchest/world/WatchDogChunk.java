package eu.mshade.enderchest.world;

import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.event.ChunkUnloadEvent;
import eu.mshade.enderframe.event.WatchdogSeeEvent;
import eu.mshade.enderframe.event.WatchdogUnseeEvent;
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
                    WatchdogUnseeEvent watchdogUnseeEvent = new WatchdogUnseeEvent(chunkBuffer);
                    EnderFrame.get().getEnderFrameEventBus().publish(watchdogUnseeEvent);
                    if (!watchdogUnseeEvent.isCancelled()) {
                        this.watchDogChunk.remove(chunkBuffer);
                        chunkBuffer.getWorldBuffer().flushChunkBuffer(chunkBuffer);
                    }
                }
            });
        }, 0, 500, TimeUnit.MILLISECONDS);


    }

    public void addChunkBuffer(ChunkBuffer chunkBuffer){
        WatchdogSeeEvent watchdogSeeEvent = new WatchdogSeeEvent(chunkBuffer);
        EnderFrame.get().getEnderFrameEventBus().publish(watchdogSeeEvent);

        if (!watchdogSeeEvent.isCancelled()) this.watchDogChunk.add(chunkBuffer);
    }

}
