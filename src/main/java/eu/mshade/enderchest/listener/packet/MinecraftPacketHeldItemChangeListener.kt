package eu.mshade.enderchest.listener.packet

import eu.mshade.enderframe.packetevent.MinecraftPacketHeldItemChangeEvent
import eu.mshade.mwork.event.EventListener

class MinecraftPacketHeldItemChangeListener: EventListener<MinecraftPacketHeldItemChangeEvent> {

    override fun onEvent(event: MinecraftPacketHeldItemChangeEvent) {
        val player = event.player

        player.inventory?.heldItemSlot = event.slot
    }
}