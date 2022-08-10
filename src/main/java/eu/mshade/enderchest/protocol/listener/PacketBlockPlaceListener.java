package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.item.ItemStack;
import eu.mshade.enderframe.item.Material;
import eu.mshade.enderframe.item.MaterialData;
import eu.mshade.enderframe.item.MaterialKey;
import eu.mshade.enderframe.packetevent.PacketBlockPlaceEvent;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.enderframe.world.Vector;
import eu.mshade.enderframe.world.World;
import eu.mshade.enderframe.world.block.BlockFace;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import io.netty.channel.Channel;

public class PacketBlockPlaceListener implements EventListener<PacketBlockPlaceEvent> {

    @Override
    public void onEvent(PacketBlockPlaceEvent event, ParameterContainer eventContainer) {
        ProtocolPipeline protocolPipeline = ProtocolPipeline.get();
        Channel channel = eventContainer.getContainer(Channel.class);
        Player player = protocolPipeline.getPlayer(channel);

        World world = player.getLocation().getWorld();
        BlockFace blockFace = event.getBlockFace();
        Vector blockPosition = event.getBlockPosition().add(blockFace.getVector());
        ItemStack itemStack = event.getItemStack();
        MaterialKey material = itemStack.getMaterial();

        if (material == null || material.equals(Material.AIR)) return;


        world.setBlock(blockPosition.getBlockX(), blockPosition.getBlockY(), blockPosition.getBlockZ(), material);

        player.getLocation().getChunk().join().notify(agent -> {
            if (agent instanceof Player target) {
                target.getSessionWrapper().sendBlockChange(blockPosition, material);
            }
        });


    }
}
