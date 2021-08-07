package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.EnderFrameSession;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.event.entity.PacketMoveEvent;
import eu.mshade.enderframe.event.entity.PacketMoveType;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Position;
import eu.mshade.enderframe.world.WorldBuffer;
import eu.mshade.enderman.packet.play.*;
import eu.mshade.mwork.event.EventContainer;
import eu.mshade.mwork.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketMoveHandler implements EventListener<PacketMoveEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketMoveHandler.class);
    private DedicatedEnderChest dedicatedEnderChest;

    public PacketMoveHandler(DedicatedEnderChest dedicatedEnderChest) {
        this.dedicatedEnderChest = dedicatedEnderChest;
    }

    @Override
    public void onEvent(PacketMoveEvent event, EventContainer eventContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = eventContainer.getContainer(EnderFrameSessionHandler.class);
        EnderFrameSession enderFrameSession = enderFrameSessionHandler.getEnderFrameSession();

        Position position = event.getPosition();
        Location before = enderFrameSession.getLocation().clone();
        Location now = enderFrameSession.getLocation();
        WorldBuffer world = now.getWorld();
        switch (event.getPacketMoveType()) {
            case LOOK:
                enderFrameSession.getLocation()
                        .setYaw(position.getYaw())
                        .setPitch(position.getPitch());
                break;
            case POSITION:
                enderFrameSession.getLocation()
                        .setX(position.getX())
                        .setY(position.getY())
                        .setZ(position.getZ());
                break;
            case POSITION_AND_LOOK:
                enderFrameSession.getLocation()
                        .setX(position.getX())
                        .setY(position.getY())
                        .setZ(position.getZ())
                        .setYaw(position.getYaw())
                        .setPitch(position.getPitch());
        }

        if (before.getChunkX() != now.getChunkX() || before.getChunkZ() != now.getChunkZ()) {
            enderFrameSession.sendSquareChunk(10, now.getChunkX(), now.getChunkZ(), now.getWorld());

        }

        Player player = world.getPlayer(enderFrameSessionHandler);
        int x1 = (int) (now.getX() * 32);
        int x2 = (int) (before.getX() * 32);
        int y1 = (int) (now.getY() * 32);
        int y2 = (int) (before.getY() * 32);
        int z1 = (int) (now.getZ() * 32);
        int z2 = (int) (before.getZ() * 32);

        int deltaX = x1 - x2;
        int deltaY = y1 - y2;
        int deltaZ = z1 - z2;
        byte yaw1 = (byte) (now.getYaw() % 360 / 360 * 256);
        byte pitch1 = (byte) (now.getPitch() % 360 / 360 * 256);

        boolean teleport = hasOverflow(deltaX) || hasOverflow(deltaY) || hasOverflow(deltaZ);
        if (!teleport) {
            if (event.getPacketMoveType() == PacketMoveType.LOOK) {
                player.getViewers().forEach(target -> {
                    target.getEnderFrameSessionHandler().sendPacket(new PacketOutEntityLook(now.getWorld().getPlayer(enderFrameSessionHandler).getEntityId(), yaw1, pitch1, position.isGround()));
                    target.getEnderFrameSessionHandler().sendPacket(new PacketOutEntityHeadLook(now.getWorld().getPlayer(enderFrameSessionHandler).getEntityId(), yaw1));
                });
            }
            if (event.getPacketMoveType() == PacketMoveType.POSITION_AND_LOOK) {
                player.getViewers().forEach(target -> {
                    target.getEnderFrameSessionHandler().sendPacket(new PacketOutEntityLookRelativeMove(now.getWorld().getPlayer(enderFrameSessionHandler).getEntityId(), deltaX, deltaY, deltaZ, yaw1, pitch1, position.isGround()));
                    target.getEnderFrameSessionHandler().sendPacket(new PacketOutEntityHeadLook(now.getWorld().getPlayer(enderFrameSessionHandler).getEntityId(), yaw1));
                });
            } else if (event.getPacketMoveType().equals(PacketMoveType.POSITION)) {
                player.getViewers().forEach(target -> target.getEnderFrameSessionHandler().sendPacket(new PacketOutEntityRelativeMove(now.getWorld().getPlayer(enderFrameSessionHandler).getEntityId(), deltaX, deltaY, deltaZ, position.isGround())));
            }
        } else {
            player.setLocation(now);
            player.getViewers().forEach(target -> target.getEnderFrameSessionHandler().sendPacket(new PacketOutEntityTeleport(player, position.isGround())));
        }


    }

    public boolean hasOverflow(int value) {
        return value > Byte.MAX_VALUE || value < Byte.MIN_VALUE;
    }
}
