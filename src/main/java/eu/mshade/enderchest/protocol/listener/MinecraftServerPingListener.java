package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.packetevent.MinecraftPacketServerPingEvent;
import eu.mshade.enderframe.protocol.MinecraftSession;
import eu.mshade.enderframe.protocol.temp.packet.MinecraftPacketOutPong;
import eu.mshade.mwork.event.EventListener;

public class MinecraftServerPingListener implements EventListener<MinecraftPacketServerPingEvent> {

    @Override
    public void onEvent(MinecraftPacketServerPingEvent event) {
        MinecraftSession minecraftSession = event.getMinecraftSession();
        minecraftSession.sendPacket(new MinecraftPacketOutPong(event.getPayload()));
    }
}
