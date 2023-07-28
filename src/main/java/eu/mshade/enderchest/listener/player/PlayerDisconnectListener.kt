package eu.mshade.enderchest.listener.player

import eu.mshade.enderchest.EnderChest
import eu.mshade.enderframe.MinecraftServer
import eu.mshade.enderframe.PlayerInfoBuilder
import eu.mshade.enderframe.PlayerInfoType
import eu.mshade.enderframe.entity.Entity
import eu.mshade.enderframe.entity.EntityTracker
import eu.mshade.enderframe.event.PlayerDisconnectEvent
import eu.mshade.enderframe.inventory.InventoryTracker
import eu.mshade.mwork.event.EventListener
import org.slf4j.LoggerFactory

class PlayerDisconnectListener : EventListener<PlayerDisconnectEvent> {

    override fun onEvent(event: PlayerDisconnectEvent) {
        val player = event.minecraftSession.player

        player.leaveTickBus()

        player.getLookAtEntity().forEach { entity ->
            player.removeWatcher(entity)
            entity.removeWatcher(player)
        }

        EntityTracker.unTrack(player)

        for (chunk in player.lookAtChunks) {
            chunk.removeWatcher(player)
        }
        for (scoreboard in player.getLookAtScoreboard()) {
            scoreboard.removeWatcher(player)
        }
        val worldBorder = player.getWorldBorder()
        worldBorder?.removeWatcher(player)

        player.getLookAtScoreboard().clear()
        player.lookAtChunks.clear()

        MinecraftServer.removePlayer(player)

        val playerInfoBuilder = PlayerInfoBuilder.of(PlayerInfoType.REMOVE_PLAYER)
            .withPlayer(player)

        MinecraftServer.getPlayers().forEach { target -> target.minecraftSession.sendPlayerInfo(playerInfoBuilder) }
        InventoryTracker.remove(player.inventory!!)

        Entity.ID.flushId(player.getEntityId())
        logger.info("{} leave server", player.name)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PlayerDisconnectListener::class.java)
    }
}
