package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.packetevent.PacketMoveEvent;
import eu.mshade.enderframe.world.Location;
import eu.mshade.mwork.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketMoveHandler implements EventListener<PacketMoveEvent> {

    private static Logger LOGGER = LoggerFactory.getLogger(PacketMoveHandler.class);

    @Override
    public void onEvent(PacketMoveEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation().clone();

        location.setX(event.getX());
        location.setY(event.getY());
        location.setZ(event.getZ());

        player.setLocation(location);

        /*
        for (Player viewer : player.getViewers()) {
            viewer.getEnderFrameSession().sendMove(player);
        }

        Emerald emerald = enderChest.getEmerald();
        Topic topic = emerald.getTopicRepository().createTopic(ShulkerPacketType.ENTITY_MOVE);
        emerald.sendPacket(topic, new ShulkerPacketEntityMove(player.getUniqueId(), player.getLocation()));

         */

        //enderChest.getDedicatedShulker().getShulkerSession().sendPacket(new ShulkerPacketMove(player.getUniqueId(), player.getLocation()));
    }
}
