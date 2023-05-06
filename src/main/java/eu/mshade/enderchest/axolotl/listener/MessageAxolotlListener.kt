package eu.mshade.enderchest.axolotl.listener

import eu.mshade.axolotl.event.ChatMessageAxolotlEvent
import eu.mshade.enderchest.EnderChest
import eu.mshade.mwork.event.EventListener

class MessageAxolotlListener: EventListener<ChatMessageAxolotlEvent> {

    override fun onEvent(event: ChatMessageAxolotlEvent) {
        val player = EnderChest.minecraftServer.getPlayer(event.uid)
        player?.minecraftSession?.sendMessage(event.textComponent, event.textPosition)
    }

}