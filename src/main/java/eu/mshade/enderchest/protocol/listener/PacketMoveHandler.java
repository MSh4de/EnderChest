package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderframe.EnderFrameSession;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.packetevent.PacketMoveEvent;
import eu.mshade.enderframe.world.Location;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

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

        //enderChest.getDedicatedShulker().getShulkerSession().sendPacket(new ShulkerPacketMove(player.getUniqueId(), player.getLocation()));
    }
}
