package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.EnderFrameSession;
import eu.mshade.enderframe.PlayerInfoBuilder;
import eu.mshade.enderframe.PlayerInfoType;
import eu.mshade.enderframe.event.entity.PacketQuitEvent;
import eu.mshade.mwork.event.EventContainer;
import eu.mshade.mwork.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketQuitHandler implements EventListener<PacketQuitEvent> {

    private static Logger logger = LoggerFactory.getLogger(PacketQuitHandler.class);

    private final DedicatedEnderChest dedicatedEnderChest;

    public PacketQuitHandler(DedicatedEnderChest dedicatedEnderChest) {
        this.dedicatedEnderChest = dedicatedEnderChest;
    }

    @Override
    public void onEvent(PacketQuitEvent event, EventContainer eventContainer) {
        EnderFrameSession enderFrameSession = event.getEnderFrameSession();
        logger.info(String.format("%s left server", enderFrameSession.getGameProfile().getName()));
        enderFrameSession.getChunkBuffers().forEach(chunkBuffer -> {
            enderFrameSession.sendUnloadChunk(chunkBuffer);
        });

        dedicatedEnderChest.removePlayer(enderFrameSession);
        dedicatedEnderChest.getEnderFrameSessions().forEach(target -> {
            target.sendPlayerInfo(PlayerInfoBuilder.of(PlayerInfoType.REMOVE_PLAYER).withPlayer(enderFrameSession));
        });
    }

    /*
    @Override
    public void handle(PacketQuitEvent packetQuitEvent, DispatcherContainer dispatcherContainer) {
            EnderFrameSession enderFrameSession = packetQuitEvent.getEnderFrameSession();
            logger.info(String.format("%s left server", enderFrameSession.getGameProfile().getName()));
            enderFrameSession.getChunkBuffers().forEach(chunkBuffer -> {
                chunkBuffer.getViewers().remove(enderFrameSession);
            });
            dedicatedEnderChest.removePlayer(enderFrameSession);
            dedicatedEnderChest.getEnderFrameSessions().forEach(target -> {
                target.sendPlayerInfo(PlayerInfoBuilder.of(PlayerInfoType.REMOVE_PLAYER).withPlayer(enderFrameSession));
            });
    }

     */
}
