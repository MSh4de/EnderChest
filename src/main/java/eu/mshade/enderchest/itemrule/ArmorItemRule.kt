package eu.mshade.enderchest.itemrule

import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.inventory.EquipmentSlot
import eu.mshade.enderframe.item.*

class ArmorItemRule: ItemRule {

    override fun apply(player: Player, itemStack: ItemStack) {
        val material = itemStack.material
        if (!material.inMaterialCategories(MaterialTag.ITEM)){
            return
        }
        val equipmentSlot = material.equipmentSlot

        val oldItemStack = player.inventory?.getItemStack(equipmentSlot)
        if (oldItemStack == null){
            player.inventory?.setItemStack(equipmentSlot, itemStack)
            player.inventory?.heldItemSlot?.let {  player.inventory?.deleteItemStack(it) }
        }

    }

    override fun canApply(material: MaterialKey): Boolean {
        return material.equipmentSlot == EquipmentSlot.HELMET || material.equipmentSlot == EquipmentSlot.CHEST_PLATE || material.equipmentSlot == EquipmentSlot.LEGGINGS || material.equipmentSlot == EquipmentSlot.BOOTS
    }


}