package eu.mshade.enderchest.listener.packet;

import eu.mshade.enderframe.GameMode;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.item.Material;
import eu.mshade.enderframe.packetevent.MinecraftPacketPlayerDiggingEvent;
import eu.mshade.enderframe.world.Vector;
import eu.mshade.enderframe.world.World;
import eu.mshade.enderframe.world.block.Block;
import eu.mshade.enderframe.world.block.BlockFace;
import eu.mshade.enderframe.world.block.DiggingStatus;
import eu.mshade.mwork.event.EventListener;

public class MinecraftPacketPlayerDiggingListener implements EventListener<MinecraftPacketPlayerDiggingEvent> {

    @Override
    public void onEvent(MinecraftPacketPlayerDiggingEvent event) {
        Player player = event.getPlayer();

        World world = player.getLocation().getWorld();
        BlockFace blockFace = event.getBlockFace();
        Vector blockPosition = event.getBlockPosition();
        GameMode gameMode = player.getGameMode();


        if (gameMode == GameMode.CREATIVE) {
            breakBlock(player, world, blockPosition);
        }


        if (gameMode == GameMode.SURVIVAL || gameMode == GameMode.ADVENTURE){
            System.out.println(event.getDiggingStatus());

            if (event.getDiggingStatus() == DiggingStatus.FINISHED) {
                breakBlock(player, world, blockPosition);
            }
        }
    }

    private void breakBlock(Player player, World world, Vector blockPosition) {
        Block block = world.getBlock(blockPosition);
        if (block.getMaterial().equals(Material.AIR)) return;
        world.setBlock(blockPosition, Material.AIR);

        player.getLocation().getChunk().join().notify(agent -> {
            if (agent instanceof Player target) {
                target.getMinecraftSession().sendBlockChange(blockPosition, Material.AIR);
            }
        });
    }

}
