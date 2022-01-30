package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderchest.emerald.Emerald;
import eu.mshade.enderframe.EnderFrameSession;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.packetevent.PacketMoveEvent;
import eu.mshade.enderframe.world.Location;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import eu.mshade.shulker.packet.ShulkerPacketEntityMove;
import eu.mshade.shulker.protocol.ShulkerPacketType;
import eu.mshade.shulker.protocol.Topic;

public class PacketMoveHandler implements EventListener<PacketMoveEvent> {

    private EnderChest enderChest;

    public PacketMoveHandler(EnderChest enderChest) {
        this.enderChest = enderChest;
    }

    @Override
    public void onEvent(PacketMoveEvent event, ParameterContainer eventContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = eventContainer.getContainer(EnderFrameSessionHandler.class);
        EnderFrameSession enderFrameSession = enderFrameSessionHandler.getEnderFrameSession();
        Player player = enderFrameSession.getPlayer();
        Location location = player.getLocation().clone();

        location.setX(event.getX());
        location.setY(event.getY());
        location.setZ(event.getZ());

        player.setUnsafeLocation(location);

        for (Player viewer : player.getViewers()) {
            viewer.getEnderFrameSession().sendMove(player);
        }

        Emerald emerald = enderChest.getEmerald();
        Topic topic = emerald.getTopicRepository().createTopic(ShulkerPacketType.ENTITY_MOVE);
        emerald.sendPacket(topic, new ShulkerPacketEntityMove(player.getUniqueId(), player.getLocation()));

        //enderChest.getDedicatedShulker().getShulkerSession().sendPacket(new ShulkerPacketMove(player.getUniqueId(), player.getLocation()));
    }
}
