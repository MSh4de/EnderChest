package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.item.Material;
import eu.mshade.enderframe.item.MaterialData;
import eu.mshade.enderframe.packetevent.PacketPlayerDiggingEvent;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.enderframe.world.Vector;
import eu.mshade.enderframe.world.World;
import eu.mshade.enderframe.world.block.BlockFace;
import eu.mshade.enderframe.world.block.DiggingStatus;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import io.netty.channel.Channel;

public class PacketPlayerDiggingListener implements EventListener<PacketPlayerDiggingEvent> {

    @Override
    public void onEvent(PacketPlayerDiggingEvent event, ParameterContainer eventContainer) {
        ProtocolPipeline protocolPipeline = ProtocolPipeline.get();
        Channel channel = eventContainer.getContainer(Channel.class);
        Player player = protocolPipeline.getPlayer(channel);

        World world = player.getLocation().getWorld();
        BlockFace blockFace = event.getBlockFace();
        Vector blockPosition = event.getBlockPosition();

        if (event.getDiggingStatus() == DiggingStatus.STARTED) {
            world.setBlock(blockPosition.getBlockX(), blockPosition.getBlockY(), blockPosition.getBlockZ(), Material.AIR);

            player.getLocation().getChunk().join().notify(agent -> {
                if (agent instanceof Player target) {
                    target.getSessionWrapper().sendBlockChange(blockPosition, Material.AIR);
                }
            });

        }
    }
}
