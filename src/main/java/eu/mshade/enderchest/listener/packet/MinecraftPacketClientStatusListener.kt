package eu.mshade.enderchest.listener.packet

import eu.mshade.enderframe.ClientStatus
import eu.mshade.enderframe.packetevent.MinecraftPacketClientStatusEvent
import eu.mshade.mwork.event.EventListener

class MinecraftPacketClientStatusListener: EventListener<MinecraftPacketClientStatusEvent> {
    override fun onEvent(event: MinecraftPacketClientStatusEvent) {
        val player = event.player
        if (event.clientStatus === ClientStatus.OPEN_INVENTORY_ACHIEVEMENT){
            player.openedInventory = player.inventory
        }
    }
}