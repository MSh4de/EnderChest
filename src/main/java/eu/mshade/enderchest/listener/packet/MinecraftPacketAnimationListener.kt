package eu.mshade.enderchest.listener.packet

import eu.mshade.enderframe.EnderFrame
import eu.mshade.enderframe.MinecraftServer
import eu.mshade.enderframe.animation.AnimationType
import eu.mshade.enderframe.event.animation.SwingArmEvent
import eu.mshade.enderframe.packetevent.MinecraftPacketAnimationEvent
import eu.mshade.mwork.event.EventListener

class MinecraftPacketAnimationListener : EventListener<MinecraftPacketAnimationEvent> {

    override fun onEvent(event: MinecraftPacketAnimationEvent) {
        val animationType = event.animationType

        when (animationType) {
            AnimationType.SWING_ARM -> MinecraftServer.getMinecraftEvent().publish(SwingArmEvent(event.player))
            else -> {
                println("Unhandled animation type: $animationType")
            }
        }
    }
}