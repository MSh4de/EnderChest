package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderchest.emerald.Emerald;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.packetevent.PacketLookEvent;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.enderframe.world.Location;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import eu.mshade.shulker.packet.ShulkerPacketEntityLook;
import eu.mshade.shulker.protocol.ShulkerPacketType;
import eu.mshade.shulker.protocol.Topic;
import io.netty.channel.Channel;

public class PacketLookHandler implements EventListener<PacketLookEvent> {


    @Override
    public void onEvent(PacketLookEvent event, ParameterContainer parameterContainer) {
        Channel channel = parameterContainer.getContainer(Channel.class);
        Player player = ProtocolPipeline.get().getPlayer(channel);
        Location location = player.getLocation().clone();

        location.setYaw(event.getYaw());
        location.setPitch(event.getPitch());

        player.setLocation(location);

        /*
        for (Player viewer : player.getViewers()) {
            EnderFrameSession viewerEnderFrameSession = viewer.getEnderFrameSession();
            viewerEnderFrameSession.sendMoveAndLook(player);
            viewerEnderFrameSession.sendHeadLook(player);
        }

         */

        /*
        Emerald emerald = enderChest.getEmerald();
        Topic topic = emerald.getTopicRepository().createTopic(ShulkerPacketType.ENTITY_LOOK);
        emerald.sendPacket(topic, new ShulkerPacketEntityLook(player.getUniqueId(), player.getLocation()));

         */

        //enderChest.getDedicatedShulker().getShulkerSession().sendPacket(new ShulkerPacketLook(player.getUniqueId(), player.getLocation()));
    }
}
