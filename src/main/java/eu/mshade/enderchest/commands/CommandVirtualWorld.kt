package eu.mshade.enderchest.commands

import eu.mshade.enderchest.EnderChest
import eu.mshade.enderframe.commands.CommandContext
import eu.mshade.enderframe.commands.CommandExecutor
import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.mojang.chat.ChatColor
import eu.mshade.enderframe.virtualserver.VirtualWorld
import eu.mshade.enderframe.world.WorldRepository
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class CommandVirtualWorld : CommandExecutor {
    override fun onCommand(ctx: CommandContext) {
        if (ctx.sender !is Player)
            return

        if (ctx.args.size <= 1) return
        val player = ctx.sender as Player
        val location = player.getLocation()
        val command: String = ctx.args[1]
        if (command == "create") {
            if (ctx.args.size == 3) {
                val virtualWorldName: String = ctx.args[2]
                EnderChest.virtualWorldManager.createVirtualWorld(virtualWorldName, location.world)
                player.minecraftSession.sendMessage(ChatColor.GREEN.toString() + "Virtual world " + virtualWorldName + " created")
            }
        } else if (command == "list") {
            EnderChest.virtualWorldManager.getVirtualWorlds()
                .forEach(Consumer<VirtualWorld> { virtualWorld: VirtualWorld ->
                    player.minecraftSession.sendMessage(
                        ChatColor.GREEN.toString() + virtualWorld.name
                    )
                })
        } else if (command == "join") {
            if (ctx.args.size == 3) {
                val virtualWorldName: String = ctx.args[2]
                val virtualWorld = EnderChest.virtualWorldManager.getVirtualWorld(virtualWorldName)
                if (virtualWorld == null) {
                    player.minecraftSession.sendMessage(ChatColor.RED.toString() + "Virtual world not found")
                    return
                }
                CompletableFuture.runAsync {
                    player.joinWorld(virtualWorld)
                    player.minecraftSession.sendMessage(ChatColor.GREEN.toString() + "Joined virtual world " + virtualWorldName)
                }
            }
        } else if (command == "leave") {
            CompletableFuture.runAsync {
                player.minecraftSession.sendMessage(
                    ChatColor.GREEN.toString() + "Left virtual world " + player.getLocation().world.name
                )
                player.joinWorld(WorldRepository.getWorld("world"))
            }
        }
    }

}
