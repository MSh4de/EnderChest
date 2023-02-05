package eu.mshade.enderchest.listener.packet;

import eu.mshade.enderframe.packetevent.MinecraftPacketServerPingEvent;
import eu.mshade.enderframe.protocol.MinecraftSession;
import eu.mshade.enderframe.protocol.temp.packet.MinecraftPacketOutServerPong;
import eu.mshade.mwork.event.EventListener;

public class MinecraftPacketServerPingListener implements EventListener<MinecraftPacketServerPingEvent> {

    @Override
    public void onEvent(MinecraftPacketServerPingEvent event) {
        MinecraftSession minecraftSession = event.getMinecraftSession();
        minecraftSession.sendPacket(new MinecraftPacketOutServerPong(event.getPayload()));
    }
}
