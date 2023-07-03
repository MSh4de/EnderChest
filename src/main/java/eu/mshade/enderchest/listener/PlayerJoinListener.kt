package eu.mshade.enderchest.listener

import eu.mshade.enderchest.EnderChest
import eu.mshade.enderframe.attribute.Attribute
import eu.mshade.enderframe.attribute.AttributeModifier
import eu.mshade.enderframe.attribute.AttributeOperation
import eu.mshade.enderframe.effect.Effect
import eu.mshade.enderframe.effect.EffectType
import eu.mshade.enderframe.event.PlayerJoinEvent
import eu.mshade.enderframe.inventory.EquipmentSlot
import eu.mshade.enderframe.item.ItemStack
import eu.mshade.enderframe.item.ItemStackAttributeModifier
import eu.mshade.enderframe.item.Material
import eu.mshade.enderframe.item.metadata.AttributeModifiersItemStackMetadata
import eu.mshade.enderframe.item.metadata.MapDecorationsItemStackMetadata
import eu.mshade.enderframe.item.metadata.MapIsScalingItemStackMetadata
import eu.mshade.enderframe.map.MapCursorType
import eu.mshade.enderframe.map.MapDecoration
import eu.mshade.mwork.event.EventListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory

class PlayerJoinListener : EventListener<PlayerJoinEvent> {

    companion object {
        val LOGGER = LoggerFactory.getLogger(PlayerJoinListener::class.java)
    }

    override fun onEvent(event: PlayerJoinEvent) {
        val player = event.player
        val minecraftSession = player.minecraftSession

        EnderChest.metrics.addWatcher(player)

        val itemStack = ItemStack(Material.MAP)
        val metadatas = itemStack.metadatas
        val mapDecoration = MapDecoration("test", MapCursorType.WHITE_POINTER, 32.0, 32.0, 1.0)

//        metadatas.setMetadataKeyValue(MapIsScalingItemStackMetadata(false))
//        metadatas.setMetadataKeyValue(MapDecorationsItemStackMetadata(mutableListOf(mapDecoration)))

        player.inventory?.setItemStack(0, itemStack)


    }

}