package eu.mshade.enderchest.listener;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderframe.PlayerInfoBuilder;
import eu.mshade.enderframe.PlayerInfoType;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.event.PlayerQuitEvent;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.enderframe.scoreboard.Scoreboard;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.enderframe.world.border.WorldBorder;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerQuitHandler implements EventListener<PlayerQuitEvent> {

    private static final Logger logger = LoggerFactory.getLogger(PlayerQuitHandler.class);

    private EnderChest enderChest;

    public PlayerQuitHandler(EnderChest enderChest) {
        this.enderChest = enderChest;
    }

    @Override
    public void onEvent(PlayerQuitEvent event) {
        Player player = event.getSessionWrapper().getPlayer();
        player.leaveTickBus();

        for (Chunk chunk : player.getLookAtChunks()) {
            chunk.removeWatcher(player);
        }

        for (Scoreboard<?> scoreboard : player.getLookAtScoreboard()) {
            scoreboard.removeWatcher(player);
        }

        for (WorldBorder worldBorder : player.getLookAtWorldBorders()) {
            worldBorder.removeWatcher(player);
        }

        player.getLookAtScoreboard().clear();
        player.getLookAtChunks().clear();
        player.getLookAtWorldBorders().clear();

        enderChest.removePlayer(player);

        PlayerInfoBuilder playerInfoBuilder = PlayerInfoBuilder.of(PlayerInfoType.REMOVE_PLAYER)
                .withPlayer(player);
        enderChest.getPlayers().forEach(target -> {
            target.getSessionWrapper().sendPlayerInfo(playerInfoBuilder);
        });

        logger.info("{} leave server", player.getName());

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
