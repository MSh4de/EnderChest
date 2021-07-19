package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.EnderFrameSession;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.event.entity.PacketMoveEvent;
import eu.mshade.enderframe.world.ChunkBuffer;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Position;
import eu.mshade.enderframe.world.WorldBuffer;
import eu.mshade.mwork.event.EventContainer;
import eu.mshade.mwork.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PacketMoveHandler implements EventListener<PacketMoveEvent> {

    private DedicatedEnderChest dedicatedEnderChest;
    private static final Logger LOGGER = LoggerFactory.getLogger(PacketMoveHandler.class);

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

    }


}
