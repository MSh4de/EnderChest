package eu.mshade.enderchest.listener

import eu.mshade.axolotl.protocol.AxolotlSession
import eu.mshade.enderchest.EnderChest
import eu.mshade.enderchest.axolotl.AxololtConnection
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
        val player = event.sessionWrapper.player

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

        EnderChest.minecraftServer.removePlayer(player)

        val playerInfoBuilder = PlayerInfoBuilder.of(PlayerInfoType.REMOVE_PLAYER)
            .withPlayer(player)

        EnderChest.minecraftServer.getOnlinePlayers().forEach { target -> target.minecraftSession.sendPlayerInfo(playerInfoBuilder) }
        InventoryTracker.remove(player.inventory!!)
        AxololtConnection.send { axolotlSession: AxolotlSession -> axolotlSession.sendPlayerLeave(player) }

        Entity.ID.flushId(player.getEntityId())
        logger.info("{} leave server", player.name)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PlayerDisconnectListener::class.java)
    }
}
