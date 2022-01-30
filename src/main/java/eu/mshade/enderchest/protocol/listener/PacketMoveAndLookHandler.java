package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderchest.emerald.Emerald;
import eu.mshade.enderframe.EnderFrameSession;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.packetevent.PacketMoveAndLookEvent;
import eu.mshade.enderframe.world.Location;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import eu.mshade.shulker.packet.ShulkerPacketEntityMove;
import eu.mshade.shulker.packet.ShulkerPacketEntityMoveAndLook;
import eu.mshade.shulker.protocol.ShulkerPacketType;
import eu.mshade.shulker.protocol.Topic;

public class PacketMoveAndLookHandler implements EventListener<PacketMoveAndLookEvent> {

    private EnderChest enderChest;

    public PacketMoveAndLookHandler(EnderChest enderChest) {
        this.enderChest = enderChest;
    }

    @Override
    public void onEvent(PacketMoveAndLookEvent event, ParameterContainer eventContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = eventContainer.getContainer(EnderFrameSessionHandler.class);
        EnderFrameSession enderFrameSession = enderFrameSessionHandler.getEnderFrameSession();
        Player player = enderFrameSession.getPlayer();
        Location location = player.getLocation().clone();

        location.setX(event.getX());
        location.setY(event.getY());
        location.setZ(event.getZ());
        location.setYaw(event.getYaw());
        location.setPitch(event.getPitch());

        player.setUnsafeLocation(location);

        for (Player viewer : player.getViewers()) {
            EnderFrameSession viewerEnderFrameSession = viewer.getEnderFrameSession();
            viewerEnderFrameSession.sendMoveAndLook(player);
            viewerEnderFrameSession.sendHeadLook(player);
        }

        Emerald emerald = enderChest.getEmerald();
        Topic topic = emerald.getTopicRepository().createTopic(ShulkerPacketType.ENTITY_MOVE_LOOK);
        emerald.sendPacket(topic, new ShulkerPacketEntityMoveAndLook(player.getUniqueId(), player.getLocation()));

        //enderChest.getDedicatedShulker().getShulkerSession().sendPacket(new ShulkerPacketMoveAndLook(player.getUniqueId(), player.getLocation()));
    }

}
