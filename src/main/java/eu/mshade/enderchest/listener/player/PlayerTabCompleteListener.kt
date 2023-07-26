package eu.mshade.enderchest.listener.player

import eu.mshade.enderchest.EnderChest
import eu.mshade.enderframe.EnderFrame
import eu.mshade.enderframe.event.PlayerTabCompleteEvent
import eu.mshade.enderframe.mojang.chat.TextComponent
import eu.mshade.mwork.event.EventListener

class PlayerTabCompleteListener : EventListener<PlayerTabCompleteEvent> {
    override fun onEvent(event: PlayerTabCompleteEvent) {
        val text = event.text
        if (!text.startsWith("/")) {
            val current = event.args[event.currentIndex]
            EnderChest.minecraftServer.getOnlinePlayers()
                .filter { it.name?.lowercase()?.startsWith(current.lowercase()) ?: false }
                .map { TextComponent.of(it.name) }.toTypedArray().let {
                    event.player.minecraftSession.sendTabComplete(*it)
                }
        }else {
            EnderFrame.get().commandManager.getCommands().keys.filter {
                it.startsWith(text.removePrefix("/"))
            }.map { TextComponent.of("/$it") }.toTypedArray().let {
                event.player.minecraftSession.sendTabComplete(*it)
            }
        }
    }
}