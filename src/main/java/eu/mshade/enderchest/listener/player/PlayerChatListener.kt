package eu.mshade.enderchest.listener.player

import eu.mshade.axolotl.protocol.AxolotlSession
import eu.mshade.enderchest.axolotl.AxololtConnection.send
import eu.mshade.enderframe.event.PlayerChatEvent
import eu.mshade.mwork.event.EventListener

class PlayerChatListener : EventListener<PlayerChatEvent> {
    override fun onEvent(event: PlayerChatEvent) {
        send { axolotlSession: AxolotlSession ->
            axolotlSession.sendChatMessage(
                event.player,
                event.message
            )
        }
    }
}