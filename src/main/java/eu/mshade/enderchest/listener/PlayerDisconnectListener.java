package eu.mshade.enderchest.listener;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderchest.axolotl.AxololtConnection;
import eu.mshade.enderframe.PlayerInfoBuilder;
import eu.mshade.enderframe.PlayerInfoType;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.event.PlayerDisconnectEvent;
import eu.mshade.enderframe.inventory.InventoryTracker;
import eu.mshade.enderframe.scoreboard.Scoreboard;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.enderframe.world.border.WorldBorder;
import eu.mshade.mwork.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerDisconnectListener implements EventListener<PlayerDisconnectEvent> {

    private static final Logger logger = LoggerFactory.getLogger(PlayerDisconnectListener.class);

    private EnderChest enderChest;

    public PlayerDisconnectListener(EnderChest enderChest) {
        this.enderChest = enderChest;
    }

    @Override
    public void onEvent(PlayerDisconnectEvent event) {
        Player player = event.getSessionWrapper().getPlayer();
        player.leaveTickBus();

        for (Chunk chunk : player.getLookAtChunks()) {
            chunk.removeWatcher(player);
        }

        for (Scoreboard<?> scoreboard : player.getLookAtScoreboard()) {
            scoreboard.getViewers().remove(player);
        }

        player.getLookAtScoreboard().clear();
        player.getLookAtChunks().clear();

        for (WorldBorder worldBorder : player.getLookAtWorldBorders()) {
            worldBorder.getViewers().remove(player);
        }
        player.getLookAtWorldBorders().clear();

        enderChest.removePlayer(player);

        PlayerInfoBuilder playerInfoBuilder = PlayerInfoBuilder.of(PlayerInfoType.REMOVE_PLAYER)
                .withPlayer(player);
        enderChest.getPlayers().forEach(target -> {
            target.getMinecraftSession().sendPlayerInfo(playerInfoBuilder);
        });

        InventoryTracker.INSTANCE.remove(player.getInventory());


        AxololtConnection.INSTANCE.send(axolotlSession -> {
            axolotlSession.sendPlayerLeave(player);
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
