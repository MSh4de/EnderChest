package eu.mshade.enderchest.listener.player

import eu.mshade.axolotl.protocol.AxolotlSession
import eu.mshade.enderchest.EnderChest.minecraftServer
import eu.mshade.enderchest.axolotl.AxololtConnection.send
import eu.mshade.enderframe.event.PlayerChatEvent
import eu.mshade.enderframe.mojang.chat.ChatColor
import eu.mshade.mwork.event.EventListener
import org.slf4j.LoggerFactory

class PlayerChatListener : EventListener<PlayerChatEvent> {

    private val LOGGER = LoggerFactory.getLogger("CHAT")

    override fun onEvent(event: PlayerChatEvent) {
        LOGGER.info(event.player.displayName + " : " + event.message)
        send { axolotlSession: AxolotlSession ->
            axolotlSession.sendChatMessage(
                event.player,
                event.message
            )
        }
        minecraftServer.getOnlinePlayers().forEach {
            it.minecraftSession.sendMessage(
                it.displayName + " : " + ChatColor.translateAlternateColorCodes(
                    '&',
                    event.message
                )
            )
        }
    }
}