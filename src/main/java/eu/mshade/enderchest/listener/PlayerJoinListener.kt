package eu.mshade.enderchest.listener

import eu.mshade.enderframe.event.PlayerJoinEvent
import eu.mshade.enderframe.item.Enchantment
import eu.mshade.enderframe.item.EnchantmentType
import eu.mshade.enderframe.item.ItemStack
import eu.mshade.enderframe.item.Material
import eu.mshade.enderframe.item.metadata.EnchantmentsItemStackMetadata
import eu.mshade.enderframe.item.metadata.NameItemStackMetadata
import eu.mshade.enderframe.mojang.chat.TextComponent
import eu.mshade.enderframe.scoreboard.ScoreboardSidebar
import eu.mshade.enderman.packet.play.world.MinecraftPacketOutTimeUpdate
import eu.mshade.mwork.event.EventListener
import org.slf4j.LoggerFactory

class PlayerJoinListener : EventListener<PlayerJoinEvent> {

    companion object {
        val LOGGER = LoggerFactory.getLogger(PlayerJoinListener::class.java)
    }

    override fun onEvent(event: PlayerJoinEvent) {
        val player = event.player
        val minecraftSession = player.minecraftSession

        val scoreboardSidebar = ScoreboardSidebar(TextComponent.of("EnderChest"))
        scoreboardSidebar.modifyLine("test") {
            it.setValue("test")
        }
        scoreboardSidebar.modifyLine("test2") {
            it.setValue("test_")
        }

        /*        val buildTeam = Team.builder()
                    .setTeamName("a")
                    .setTeamMode(TeamMode.CREATE_TEAM)
                    .setTeamDisplayName("hi")
                    .setPlayersName(listOf(player.name))
                    .buildTeam()

                minecraftSession.sendTeams(buildTeam)*/

        scoreboardSidebar.addWatcher(player)


        val itemStack = ItemStack(Material.RED_WOOL, 64)
        val metadataKeyValueBucket = itemStack.metadataKeyValueBucket
        metadataKeyValueBucket.setMetadataKeyValue(NameItemStackMetadata("Test"))


        player.inventory?.setItemStack(0, itemStack)

    }

}