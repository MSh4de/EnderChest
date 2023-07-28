package eu.mshade.enderchest.commands

import eu.mshade.enderchest.EnderChest
import eu.mshade.enderframe.commands.CommandContext
import eu.mshade.enderframe.commands.CommandExecutor
import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.world.Location

class CommandTp : CommandExecutor {
    override fun onCommand(ctx: CommandContext) {
        if (ctx.sender !is Player)
            return
        val player = ctx.sender as Player
        val location = player.getLocation()

        if (ctx.args.size == 3) {
            val x: Int = ctx.args[0].toInt()
            val y: Int = ctx.args[1].toInt()
            val z: Int = ctx.args[2].toInt()
            player.minecraftSession.teleport(Location(location.world, x.toDouble(), y.toDouble(), z.toDouble()))
        } else if (ctx.args.size == 1) {
            val name: String = ctx.args[0]
            val target = EnderChest.minecraftServer.getPlayer(name)
            if (target != null) {
                player.minecraftSession.teleport(target.getLocation())
            }
        }
    }

}
