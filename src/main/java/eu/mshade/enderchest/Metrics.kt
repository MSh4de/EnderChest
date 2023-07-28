package eu.mshade.enderchest

import eu.mshade.enderframe.MinecraftServer
import eu.mshade.enderframe.mojang.chat.ChatColor
import eu.mshade.enderframe.tick.Tickable
import eu.mshade.enderframe.world.block.TickableBlockRepository

class Metrics : Tickable() {

    val scoreboard = EnderChest.metrics
    val tickBus = EnderChest.tickBus
    val minecraftServer = MinecraftServer

    override fun tick() {
        if (!isPeriod(20)) return

        scoreboard.modifyLine("space0") {
            it.setValue(ChatColor.AQUA.toString())
        }

        scoreboard.modifyLine("tps") {
            it.setValue("TPS: ${String.format("%.2f", tickBus.tps)}")
        }

        scoreboard.modifyLine("space1") {
            it.setValue(ChatColor.GREEN.toString())
        }

        scoreboard.modifyLine("players") {
            it.setValue("Players: ${minecraftServer.getPlayers().size}")
        }

        scoreboard.modifyLine("space2") {
            it.setValue(ChatColor.RED.toString())
        }


        scoreboard.modifyLine("memory") {
            //memoryUsage/maxMemory
            val memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
            val maxMemory = Runtime.getRuntime().maxMemory()
            val memoryUsageMB = memoryUsage / 1024 / 1024
            val maxMemoryMB = maxMemory / 1024 / 1024
            it.setValue("Memory: $memoryUsageMB/$maxMemoryMB MB")

        }

        scoreboard.modifyLine("space3") {
            it.setValue(ChatColor.YELLOW.toString())
        }

        val chunks = minecraftServer.getWorlds().flatMap { it.chunks }.size

        scoreboard.modifyLine("chunks") {
            it.setValue("Chunks: $chunks")
        }

        scoreboard.modifyLine("space4") {
            it.setValue(ChatColor.GOLD.toString())
        }

        val regions = minecraftServer.getWorlds().flatMap { it.regions }.size

        scoreboard.modifyLine("regions") {
            it.setValue("Regions: $regions")
        }

        scoreboard.modifyLine("space5") {
            it.setValue(ChatColor.DARK_PURPLE.toString())
        }

        val tickableBlockSize = tickBus.tickables.find { it is TickableBlockRepository }?.let {
            (it as TickableBlockRepository).tickableBlockEntries.size
        } ?: 0
        scoreboard.modifyLine("tickBus") {
            it.setValue("Tickable count: $tickableBlockSize")
        }

    }
}