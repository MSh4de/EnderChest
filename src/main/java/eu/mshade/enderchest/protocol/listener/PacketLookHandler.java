package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderchest.emerald.Emerald;
import eu.mshade.enderframe.EnderFrameSession;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.packetevent.PacketLookEvent;
import eu.mshade.enderframe.world.Location;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import eu.mshade.shulker.packet.ShulkerPacketEntityLook;
import eu.mshade.shulker.packet.ShulkerPacketEntityMove;
import eu.mshade.shulker.protocol.ShulkerPacketType;
import eu.mshade.shulker.protocol.Topic;

public class PacketLookHandler implements EventListener<PacketLookEvent> {

    private EnderChest enderChest;

    public PacketLookHandler(EnderChest enderChest) {
        this.enderChest = enderChest;
    }

    @Override
    public void onEvent(PacketLookEvent event, ParameterContainer eventContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = eventContainer.getContainer(EnderFrameSessionHandler.class);
        EnderFrameSession enderFrameSession = enderFrameSessionHandler.getEnderFrameSession();
        Player player = enderFrameSession.getPlayer();
        Location location = player.getLocation().clone();

        location.setYaw(event.getYaw());
        location.setPitch(event.getPitch());

        player.setUnsafeLocation(location);

        for (Player viewer : player.getViewers()) {
            EnderFrameSession viewerEnderFrameSession = viewer.getEnderFrameSession();
            viewerEnderFrameSession.sendMoveAndLook(player);
            viewerEnderFrameSession.sendHeadLook(player);
        }

        Emerald emerald = enderChest.getEmerald();
        Topic topic = emerald.getTopicRepository().createTopic(ShulkerPacketType.ENTITY_LOOK);
        emerald.sendPacket(topic, new ShulkerPacketEntityLook(player.getUniqueId(), player.getLocation()));

        //enderChest.getDedicatedShulker().getShulkerSession().sendPacket(new ShulkerPacketLook(player.getUniqueId(), player.getLocation()));
    }
}
