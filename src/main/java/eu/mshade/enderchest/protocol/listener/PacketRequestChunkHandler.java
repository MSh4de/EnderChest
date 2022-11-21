package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.packetevent.MinecraftPacketMoveEvent;
import eu.mshade.enderframe.world.Location;
import eu.mshade.mwork.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PacketRequestChunkHandler implements EventListener<MinecraftPacketMoveEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketRequestChunkHandler.class);


    @Override
    public void onEvent(MinecraftPacketMoveEvent event) {
        Player player = event.getPlayer();
        Location before = player.getBeforeLocation();
        Location now = player.getLocation();

        if (before.getChunkX() != now.getChunkX() || before.getChunkZ() != now.getChunkZ()) {
            int render = (before.distance(now) < 5 ? 10 : 3);

            /*
            synchronized (this){
                enderFrameSession.sendSquareChunk(render, now.getChunkX(), now.getChunkZ(), now.getWorld());
            }

             */


        }

    }


}
