package eu.mshade.enderchest.listener.packet;

import eu.mshade.enderframe.GameMode;
import eu.mshade.enderframe.item.MaterialKey;
import eu.mshade.enderframe.protocol.MinecraftSession;
import eu.mshade.enderframe.world.effect.WorldEffectType;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.item.Material;
import eu.mshade.enderframe.packetevent.MinecraftPacketPlayerDiggingEvent;
import eu.mshade.enderframe.world.Vector;
import eu.mshade.enderframe.world.World;
import eu.mshade.enderframe.world.block.Block;
import eu.mshade.enderframe.world.block.BlockFace;
import eu.mshade.enderframe.world.block.DiggingStatus;
import eu.mshade.mwork.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinecraftPacketPlayerDiggingListener implements EventListener<MinecraftPacketPlayerDiggingEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinecraftPacketPlayerDiggingListener.class);

    @Override
    public void onEvent(MinecraftPacketPlayerDiggingEvent event) {
        Player player = event.getPlayer();

        World world = player.getLocation().getWorld();
        BlockFace blockFace = event.getBlockFace();
        Vector blockPosition = event.getBlockPosition();
        GameMode gameMode = player.getGameMode();


        Block block = world.getBlock(blockPosition);
        MaterialKey material = block.getMaterial();
        //calculate estimated time to break block with tool
        int timeToBreak = (int) (material.getHardness() * 1500);

        LOGGER.info("Player {} is digging {} with {} in {}ms", player.getName(), material.getNamespacedKey(), blockFace, timeToBreak);

        if (gameMode == GameMode.CREATIVE) {
            breakBlock(player, world, blockPosition);
        }


        if (gameMode == GameMode.SURVIVAL || gameMode == GameMode.ADVENTURE){

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
                MinecraftSession minecraftSession = target.getMinecraftSession();
                minecraftSession.sendBlockChange(blockPosition, Material.AIR);
                minecraftSession.sendWorldEffect(WorldEffectType.STEP_SOUND, blockPosition, block.getMaterial(), false);
            }
        });
    }

}
