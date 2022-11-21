package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.packetevent.MinecraftPacketMoveAndLookEvent;
import eu.mshade.enderframe.world.Location;
import eu.mshade.mwork.event.EventListener;

public class PacketMoveAndLookHandler implements EventListener<MinecraftPacketMoveAndLookEvent> {


    @Override
    public void onEvent(MinecraftPacketMoveAndLookEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation().clone();

        location.setX(event.getX());
        location.setY(event.getY());
        location.setZ(event.getZ());
        location.setYaw(event.getYaw());
        location.setPitch(event.getPitch());

        player.setLocation(location);

        /*
        for (Player viewer : player.getViewers()) {
            EnderFrameSession viewerEnderFrameSession = viewer.getEnderFrameSession();
            viewerEnderFrameSession.sendMoveAndLook(player);
            viewerEnderFrameSession.sendHeadLook(player);
        }

        Emerald emerald = enderChest.getEmerald();
        Topic topic = emerald.getTopicRepository().createTopic(ShulkerPacketType.ENTITY_MOVE_LOOK);
        emerald.sendPacket(topic, new ShulkerPacketEntityMoveAndLook(player.getUniqueId(), player.getLocation()));

         */

        //enderChest.getDedicatedShulker().getShulkerSession().sendPacket(new ShulkerPacketMoveAndLook(player.getUniqueId(), player.getLocation()));
    }

}
