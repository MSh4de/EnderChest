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
import eu.mshade.mwork.event.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class PacketMoveHandler implements EventListener<PacketMoveEvent> {

    public static final HashMap<EnderFrameSession, Integer> linkedMob = new LinkedHashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(PacketMoveHandler.class);
    private DedicatedEnderChest dedicatedEnderChest;

    public PacketMoveHandler(DedicatedEnderChest dedicatedEnderChest) {
        this.dedicatedEnderChest = dedicatedEnderChest;
    }

    @Override
    public void onEvent(PacketMoveEvent event, ParameterContainer parameterContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = parameterContainer.getContainer(EnderFrameSessionHandler.class);
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
        int x1 = floor(now.getX() * 32);
        int x2 = floor(before.getX() * 32);
        int y1 = floor(now.getY() * 32);
        int y2 = floor(before.getY() * 32);
        int z1 = floor(now.getZ() * 32);
        int z2 = floor(before.getZ() * 32);

        int deltaX = (x1 - x2);
        int deltaY = (y1 - y2);
        int deltaZ = (z1 - z2);
        int yaw1 = (int) (now.getYaw() % 360 / 360 * 256);
        int pitch1 = (int) (now.getPitch() % 360 / 360 * 256);
        boolean teleport = hasOverflow(deltaX) || hasOverflow(deltaY) || hasOverflow(deltaZ);


            if (event.getPacketMoveType() == PacketMoveType.LOOK || event.getPacketMoveType() == PacketMoveType.POSITION_AND_LOOK) {
                player.getViewers().forEach(target -> {
                    target.getEnderFrameSessionHandler().sendPacket(new PacketOutEntityLook(player.getEntityId(), yaw1, pitch1, position.isGround()));
                    target.getEnderFrameSessionHandler().sendPacket(new PacketOutEntityHeadLook(player.getEntityId(), yaw1));
                });
            }
        if (!teleport) {
            if (event.getPacketMoveType() == PacketMoveType.POSITION_AND_LOOK) {
                player.getViewers().forEach(target -> {
                    target.getEnderFrameSessionHandler().sendPacket(new PacketOutEntityLookRelativeMove(player.getEntityId(), (byte) deltaX, (byte) deltaY, (byte) deltaZ, yaw1, pitch1, position.isGround()));
                });
            } else if (event.getPacketMoveType().equals(PacketMoveType.POSITION)) {
                player.getViewers().forEach(target -> target.getEnderFrameSessionHandler().sendPacket(new PacketOutEntityRelativeMove(player.getEntityId(), (byte) deltaX, (byte) deltaY, (byte) deltaZ, position.isGround())));
            }
        } else {
            player.getViewers().forEach(target ->{
                target.getEnderFrameSessionHandler().sendPacket(new PacketOutEntityTeleport(player.getEntityId(), player, position.isGround()));
            });
        }
    }

    public boolean hasOverflow(int value) {
        return value > 3 || value < -3;
    }

    public int floor(double d0) {
        int i = (int) d0;

        return d0 < (double) i ? i - 1 : i;
    }
}
