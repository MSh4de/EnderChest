package eu.mshade.enderchest.axolotl.listener

import eu.mshade.axolotl.packet.AxolotlPacketInChatMessage
import eu.mshade.enderchest.EnderChest
import eu.mshade.mwork.event.EventListener

class AxolotlPacketInChatMessageHandler : EventListener<AxolotlPacketInChatMessage> {

    override fun onEvent(event: AxolotlPacketInChatMessage) {
        EnderChest.players.stream().filter { it.uniqueId == event.player }.forEach { it.sessionWrapper.sendMessage(event.message, event.position) }
    }
}