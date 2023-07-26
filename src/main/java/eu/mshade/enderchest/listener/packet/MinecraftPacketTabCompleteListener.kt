package eu.mshade.enderchest.listener.packet

import eu.mshade.enderframe.EnderFrame
import eu.mshade.enderframe.event.PlayerTabCompleteEvent
import eu.mshade.enderframe.packetevent.MinecraftPacketTabCompleteEvent
import eu.mshade.mwork.event.EventListener
import org.slf4j.LoggerFactory

class MinecraftPacketTabCompleteListener : EventListener<MinecraftPacketTabCompleteEvent> {

    private val LOGGER = LoggerFactory.getLogger(MinecraftPacketTabCompleteListener::class.java)
    override fun onEvent(event: MinecraftPacketTabCompleteEvent) {
        LOGGER.debug("Tab complete event: ${event.text}")
        val args = event.text.split(" ")
        if (event.text.startsWith("/")) {
            // NON-CANCELABLE
            // |
            // V
            val command = args[0].removePrefix("/")
            LOGGER.debug("Parsed command name: $command")
            EnderFrame.get().commandManager.getCommand(command)?.let { cmd ->
                cmd.tabComplete?.matches(event.text, args.lastIndex, args.slice(1 until args.size).toTypedArray())
                    ?.let {
                        event.minecraftSession.sendTabComplete(
                            *it
                        )
                    }
            }
        }

        // CANCELABLE
        // |
        // V
        EnderFrame.get().minecraftEvents.publish(
            PlayerTabCompleteEvent(
                event.minecraftSession.player,
                event.text,
                args.lastIndex,
                args.toTypedArray()
            )
        )
    }
}