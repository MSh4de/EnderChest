package eu.mshade.enderchest.itemrule

import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.inventory.EquipmentSlot
import eu.mshade.enderframe.item.ItemRule
import eu.mshade.enderframe.item.ItemStack
import eu.mshade.enderframe.item.Material
import eu.mshade.enderframe.item.MaterialKey

class ArmorItemRule: ItemRule {

    private val materialSlot = mutableMapOf<MaterialKey, EquipmentSlot>()

    init {
        register(EquipmentSlot.HELMET, Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.GOLDEN_HELMET, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET)
        register(EquipmentSlot.CHEST_PLATE, Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE)
        register(EquipmentSlot.LEGGINGS, Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS)
        register(EquipmentSlot.BOOTS, Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS)
    }

    override fun apply(player: Player, itemStack: ItemStack) {
        val equipmentSlot = materialSlot[itemStack.material] ?: return

        val oldItemStack = player.inventory?.getItemStack(equipmentSlot)
        if (oldItemStack == null){
            player.inventory?.setItemStack(equipmentSlot, itemStack)
            player.inventory?.heldItemSlot?.let {  player.inventory?.deleteItemStack(it) }
        }

    }

    private fun register(equipmentSlot: EquipmentSlot, vararg materials: MaterialKey){
        materials.forEach {
            materialSlot[it] = equipmentSlot
        }
    }
}