package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.EnderFrameSession;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.packetevent.PacketMoveEvent;
import eu.mshade.enderframe.world.Location;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PacketRequestChunkHandler implements EventListener<PacketMoveEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketRequestChunkHandler.class);
    private DedicatedEnderChest dedicatedEnderChest;

    public PacketRequestChunkHandler(DedicatedEnderChest dedicatedEnderChest) {
        this.dedicatedEnderChest = dedicatedEnderChest;
    }

    @Override
    public void onEvent(PacketMoveEvent event, ParameterContainer parameterContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = parameterContainer.getContainer(EnderFrameSessionHandler.class);
        EnderFrameSession enderFrameSession = enderFrameSessionHandler.getEnderFrameSession();
        Player player = enderFrameSession.getPlayer();
        Location before = player.getBeforeLocation();
        Location now = player.getLocation();

        if (before.getChunkX() != now.getChunkX() || before.getChunkZ() != now.getChunkZ()) {
            int render = (before.distance(now) < 5 ? 10 : 3);
            enderFrameSession.sendSquareChunk(render, now.getChunkX(), now.getChunkZ(), now.getWorld());
        }

    }


}
