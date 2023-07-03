package eu.mshade.enderchest.listener.packet

import eu.mshade.enderchest.EnderChest
import eu.mshade.enderchest.EnderChest.minecraftServer
import eu.mshade.enderframe.EnderFrame
import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.event.BlockPlaceEvent
import eu.mshade.enderframe.item.Material
import eu.mshade.enderframe.item.MaterialTag
import eu.mshade.enderframe.packetevent.MinecraftPacketBlockPlaceEvent
import eu.mshade.enderframe.world.Vector
import eu.mshade.enderframe.world.block.Block
import eu.mshade.enderframe.world.block.BlockFace
import eu.mshade.mwork.event.EventListener
import org.slf4j.LoggerFactory

class MinecraftPacketBlockPlaceListener : EventListener<MinecraftPacketBlockPlaceEvent> {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MinecraftPacketBlockPlaceListener::class.java)
    }

    val tickableBlocks = EnderChest.minecraftServer.getTickableBlocks()

    override fun onEvent(event: MinecraftPacketBlockPlaceEvent) {
        val player = event.player
        val world = player.getLocation().world
        val blockFace = event.blockFace
        var blockPosition = event.blockPosition
        val itemStack = player.inventory?.itemInHand

        if (event.blockPosition == Vector.ZERO && itemStack != null) {
            //TODO: right click air with item
            return
        }

        val clickedBlock = world.getBlock(event.blockPosition)

        //TODO: make interactable if item is air
        if (clickedBlock.getMaterial().inMaterialCategories(MaterialTag.INTERACTABLE) && !player.isSneaking()) {
            val blockBehavior = minecraftServer.getBlockBehaviors().getBlockBehavior(clickedBlock.getMaterial())
            blockBehavior?.interact(player, blockPosition, blockFace, event.cursorPosition, clickedBlock, tickableBlocks)

            return
        }

        val material = itemStack?.material

        if (material == null || material.materialCategories.contains(MaterialTag.ITEM) && !material.materialCategories.contains(MaterialTag.BLOCK)) {
            //TODO: right click block with item
            return
        }

        /*        if (material.getMaterialCategories().contains(MaterialTag.ITEM) && !material.getMaterialCategories().contains(MaterialTag.BLOCK)) {
            ItemRule itemRule = EnderChest.INSTANCE.getMinecraftServer().getItemRules().getItemRule(material);

            if (itemRule != null) {
                itemRule.apply(player, itemStack);
            }

            return;
        }*/

        if (material == Material.AIR) {
            val block = world.getBlock(blockPosition)
            println(block.getMetadatas().toPrettyString(0))
            return
        }

        if (blockFace != BlockFace.NONE) {

            blockPosition = event.blockPosition.clone().add(blockFace.vector)

            if (world.getBlock(blockPosition).getMaterial() != Material.AIR) return


            val blockBehavior = minecraftServer.getBlockBehaviors().getBlockBehavior(material)
            val placedPairs = mutableListOf<Pair<Vector, Block>>()

            if (blockBehavior != null) {
                placedPairs.addAll(blockBehavior.place(player, blockPosition, blockFace, event.cursorPosition, material))
            } else {
                placedPairs.add(Pair<Vector, Block>(blockPosition, material.toBlock()))
            }

            val blockPlaceEvent = BlockPlaceEvent(player, material.toBlock(), blockPosition)
            EnderFrame.get().minecraftEvents.publish(blockPlaceEvent)

            if (blockPlaceEvent.isCancelled || placedPairs.isEmpty()) {
                player.getLocation().chunk.join().notify(Player::class.java) {  it.minecraftSession.sendBlockChange(blockPosition, world.getBlock(blockPosition)) }
                return
            }

            for ((vector, block) in placedPairs) {
                world.setBlock(vector, block)
                player.getLocation().chunk.join().notify(Player::class.java) {  it.minecraftSession.sendBlockChange(vector, block) }
            }
        }
    }
}
