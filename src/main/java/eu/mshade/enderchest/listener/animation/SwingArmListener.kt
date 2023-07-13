package eu.mshade.enderchest.listener.animation

import eu.mshade.enderframe.animation.AnimationType
import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.event.animation.SwingArmEvent
import eu.mshade.mwork.event.EventListener

class SwingArmListener : EventListener<SwingArmEvent> {

    override fun onEvent(event: SwingArmEvent) {
        event.player.getWatchers().forEach {
            if (it is Player) it.minecraftSession.sendAnimation(event.player, AnimationType.SWING_ARM)
        }
    }
}