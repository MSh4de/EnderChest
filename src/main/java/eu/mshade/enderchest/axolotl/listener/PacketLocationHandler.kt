package eu.mshade.enderchest.axolotl.listener

import eu.mshade.axolotl.packet.AxolotlPacketInTeleport
import eu.mshade.enderchest.EnderChest
import eu.mshade.enderframe.world.WorldRepository
import eu.mshade.mwork.event.EventListener

class AxolotlPacketInTeleportHandler : EventListener<AxolotlPacketInTeleport> {

    override fun onEvent(event: AxolotlPacketInTeleport) {
        val player = EnderChest.players.stream().filter { it.uniqueId == event.entity }.findFirst().orElse(null) ?: return
        player.sessionWrapper.teleport(event.location)
    }
}