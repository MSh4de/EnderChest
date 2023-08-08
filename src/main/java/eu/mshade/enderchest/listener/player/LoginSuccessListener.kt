package eu.mshade.enderchest.listener.player

import eu.mshade.enderframe.MinecraftServer
import eu.mshade.enderframe.event.LoginSuccessEvent
import eu.mshade.enderframe.event.PrePlayerJoinEvent
import eu.mshade.mwork.event.EventListener

class LoginSuccessListener : EventListener<LoginSuccessEvent> {

    override fun onEvent(event: LoginSuccessEvent) {
        val minecraftSession = event.minecraftSession

        minecraftSession.sendCompression(256)
        minecraftSession.sendLoginSuccess()


        val minecraftEvent = MinecraftServer.getMinecraftEvent()
        minecraftEvent.publish(PrePlayerJoinEvent(minecraftSession))
    }

}