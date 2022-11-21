package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.packetevent.MinecraftPacketLookEvent;
import eu.mshade.enderframe.world.Location;
import eu.mshade.mwork.event.EventListener;

public class PacketLookHandler implements EventListener<MinecraftPacketLookEvent> {


    @Override
    public void onEvent(MinecraftPacketLookEvent event) {
        Player player = event.getPlayer();
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
