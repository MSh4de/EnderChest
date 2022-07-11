package eu.mshade.enderchest.listener;

import com.google.inject.Inject;
import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.event.PlayerQuitEvent;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.enderframe.world.Chunk;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerQuitHandler implements EventListener<PlayerQuitEvent> {

    private static final Logger logger = LoggerFactory.getLogger(PlayerQuitHandler.class);

    @Inject
    private EnderChest enderChest;

    @Override
    public void onEvent(PlayerQuitEvent event, ParameterContainer eventContainer) {
        Channel channel = eventContainer.getContainer(Channel.class);
        Player player = ProtocolPipeline.get().getPlayer(channel);
        enderChest.getTickBus().removeTickable(player);
        for (Chunk chunk : player.getLookAtChunks()) {
            chunk.getViewers().remove(player);
        }
        player.getLookAtChunks().clear();
        enderChest.removePlayer(player);

        /*
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

         */
    }

}
