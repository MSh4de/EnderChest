package eu.mshade.enderchest.listener.packet;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.event.BlockPlaceEvent;
import eu.mshade.enderframe.item.*;
import eu.mshade.enderframe.packetevent.MinecraftPacketBlockPlaceEvent;
import eu.mshade.enderframe.world.Vector;
import eu.mshade.enderframe.world.World;
import eu.mshade.enderframe.world.block.*;
import eu.mshade.mwork.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinecraftPacketBlockPlaceListener implements EventListener<MinecraftPacketBlockPlaceEvent> {

    //logger with logback
    private static final Logger LOGGER = LoggerFactory.getLogger(MinecraftPacketBlockPlaceListener.class);

    @Override
    public void onEvent(MinecraftPacketBlockPlaceEvent event) {
        Player player = event.getPlayer();
        World world = player.getLocation().getWorld();
        BlockFace blockFace = event.getBlockFace();
        Vector blockPosition;
        ItemStack itemStack = player.getInventory().getItemInHand();
        if (itemStack == null) return;
        MaterialKey material = itemStack.getMaterial();
        boolean isSneaking = player.isSneaking();

        if (material == null) return;

        if (material.getMaterialCategories().contains(MaterialTag.ITEM) && !material.getMaterialCategories().contains(MaterialTag.BLOCK)) {
            ItemRule itemRule = EnderChest.INSTANCE.getMinecraftServer().getItemRules().getItemRule(material);

            if (itemRule != null) {
                itemRule.apply(player, itemStack);
            }

            return;
        }

        if (material.equals(Material.AIR)){
            blockPosition = event.getBlockPosition();
            Block block = world.getBlock(blockPosition);

            System.out.println(block.getMetadataKeyValueBucket().toPrettyString(0));
            return;
        }

        if (blockFace != BlockFace.NONE) {

            blockPosition = event.getBlockPosition().clone().add(blockFace.getVector());

            Block block = material.toBlock();

            BlockRule blockRule = EnderChest.INSTANCE.getMinecraftServer().getBlockRules().getBlockRule(block.getMaterialKey());
            if (blockRule != null) {
                block = blockRule.apply(player.getLocation(), blockPosition, blockFace, event.getCursorPosition(), block);
            }

            if (block == null) block = Material.AIR.toBlock();




            BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(player, block, blockPosition);
            EnderFrame.get().getMinecraftEvents().publish(blockPlaceEvent);

            if (blockPlaceEvent.isCancelled()){
                player.getLocation().getChunk().join().notify(agent -> {
                    if (agent instanceof Player target) {
                        target.getMinecraftSession().sendBlockChange(blockPosition, world.getBlock(blockPosition));
                    }
                });

                return;
            }
            world.setBlock(blockPosition, block);

            Block finalBlock = block;
            player.getLocation().getChunk().join().notify(agent -> {
                if (agent instanceof Player target) {
                    target.getMinecraftSession().sendBlockChange(blockPosition, finalBlock);
                }
            });


        }


    }
}
