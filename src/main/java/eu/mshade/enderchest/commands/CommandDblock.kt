package eu.mshade.enderchest.commands

import eu.mshade.enderframe.commands.CommandContext
import eu.mshade.enderframe.commands.CommandExecutor
import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.item.MaterialKey
import eu.mshade.enderframe.mojang.chat.TextComponent
import eu.mshade.enderframe.world.Vector

class CommandDblock : CommandExecutor {
    override fun onCommand(ctx: CommandContext) {
        if (ctx.sender !is Player)
            return

        val player = ctx.sender as Player
        val location = player.getLocation()
        val id: Int = ctx.args[1].toInt()

        if (ctx.args.size == 2) {
            for (i in 0..15) {
                val materialKey = MaterialKey.from(id, i)
                val vector: Vector =
                    Vector(location.blockX, location.blockY, location.blockZ).add(i * 2, 0, 0)
                player.minecraftSession.sendUnsafeBlockChange(vector, materialKey)
            }
            for (i in 0..15) {
                val vector: Vector =
                    Vector(location.blockX, location.blockY, location.blockZ).add(i * 2, 1, 0)
                player.minecraftSession.sendUnsafeBlockChange(vector, MaterialKey.from(63))
                player.minecraftSession.sendSign(
                    vector, TextComponent.of("id: $id"), TextComponent.of(
                        "data: $i"
                    )
                )
            }
        } else if (ctx.args.size == 3) {
            val data: Int = ctx.args[2].toInt()
            val materialKey = MaterialKey.from(id, data)
            player.minecraftSession.sendUnsafeBlockChange(location.toVector(), materialKey)
        }
    }

}
