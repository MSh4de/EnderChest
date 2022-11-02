package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.item.Material;
import eu.mshade.enderframe.packetevent.PacketPlayerDiggingEvent;
import eu.mshade.enderframe.world.Vector;
import eu.mshade.enderframe.world.World;
import eu.mshade.enderframe.world.block.Block;
import eu.mshade.enderframe.world.block.BlockFace;
import eu.mshade.enderframe.world.block.DiggingStatus;
import eu.mshade.mwork.event.EventListener;

public class PacketPlayerDiggingListener implements EventListener<PacketPlayerDiggingEvent> {

    @Override
    public void onEvent(PacketPlayerDiggingEvent event) {
        Player player = event.getPlayer();

        World world = player.getLocation().getWorld();
        BlockFace blockFace = event.getBlockFace();
        Vector blockPosition = event.getBlockPosition();

        if (event.getDiggingStatus() == DiggingStatus.STARTED) {
            Block block = world.getBlock(blockPosition);
            if (block == null || block.getMaterialKey().equals(Material.AIR)) return;
            world.setBlock(blockPosition, Material.AIR);

            player.getLocation().getChunk().join().notify(agent -> {
                if (agent instanceof Player target) {
                    target.getSessionWrapper().sendBlockChange(blockPosition, Material.AIR);
                }
            });

        }
    }
}
