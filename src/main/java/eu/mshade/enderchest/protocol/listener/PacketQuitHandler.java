package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.EnderFrameSession;
import eu.mshade.enderframe.PlayerInfoBuilder;
import eu.mshade.enderframe.PlayerInfoType;
import eu.mshade.enderframe.entity.EntityIdManager;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.packetevent.PacketQuitEvent;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketQuitHandler implements EventListener<PacketQuitEvent> {

    private static final Logger logger = LoggerFactory.getLogger(PacketQuitHandler.class);

    private final DedicatedEnderChest dedicatedEnderChest;

    public PacketQuitHandler(DedicatedEnderChest dedicatedEnderChest) {
        this.dedicatedEnderChest = dedicatedEnderChest;
    }

    @Override
    public void onEvent(PacketQuitEvent event, ParameterContainer eventContainer) {
        EnderFrameSession enderFrameSession = event.getEnderFrameSession();
        Player player = enderFrameSession.getPlayer();
        logger.info(String.format("%s left server", player.getName()));
        enderFrameSession.getChunkBuffers().forEach(enderFrameSession::sendUnloadChunk);
        
        dedicatedEnderChest.removePlayer(player);
        dedicatedEnderChest.getPlayers().forEach(target -> {
            target.getEnderFrameSession().sendPlayerInfo(PlayerInfoBuilder.of(PlayerInfoType.REMOVE_PLAYER).withPlayer(player));
            target.sendMessage(String.format("%s left server", player.getGameProfile().getName()));
        });

        EntityIdManager.get().flushId(player.getEntityId());
    }

}
