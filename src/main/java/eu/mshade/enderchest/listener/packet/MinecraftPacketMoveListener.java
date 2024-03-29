package eu.mshade.enderchest.listener.packet;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.packetevent.MinecraftPacketMoveEvent;
import eu.mshade.enderframe.world.Location;
import eu.mshade.mwork.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinecraftPacketMoveListener implements EventListener<MinecraftPacketMoveEvent> {

    private static Logger LOGGER = LoggerFactory.getLogger(MinecraftPacketMoveListener.class);

    @Override
    public void onEvent(MinecraftPacketMoveEvent event) {
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
