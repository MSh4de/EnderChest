package eu.mshade.enderchest.listener

import eu.mshade.enderchest.EnderChest
import eu.mshade.enderframe.event.PlayerJoinEvent
import eu.mshade.enderframe.item.Enchantment
import eu.mshade.enderframe.item.EnchantmentType
import eu.mshade.enderframe.item.ItemStack
import eu.mshade.enderframe.item.Material
import eu.mshade.enderframe.item.metadata.EnchantmentsItemStackMetadata
import eu.mshade.enderframe.item.metadata.NameItemStackMetadata
import eu.mshade.enderframe.mojang.chat.TextComponent
import eu.mshade.enderframe.particle.ParticleBlockCrack
import eu.mshade.enderframe.scoreboard.ScoreboardSidebar
import eu.mshade.enderframe.world.Vector
import eu.mshade.enderman.packet.play.world.MinecraftPacketOutTimeUpdate
import eu.mshade.mwork.event.EventListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.slf4j.LoggerFactory

class PlayerJoinListener : EventListener<PlayerJoinEvent> {

    companion object {
        val LOGGER = LoggerFactory.getLogger(PlayerJoinListener::class.java)
    }

    override fun onEvent(event: PlayerJoinEvent) {
        val player = event.player
        val minecraftSession = player.minecraftSession

        EnderChest.metrics.addWatcher(player)

        val itemStack = ItemStack(Material.VILLAGER_SPAWN_EGG)
        player.inventory?.addItemStack(itemStack)

    }

}