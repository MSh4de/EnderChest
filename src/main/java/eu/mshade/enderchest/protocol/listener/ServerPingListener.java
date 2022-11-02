package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.packetevent.ServerPingEvent;
import eu.mshade.enderframe.protocol.SessionWrapper;
import eu.mshade.enderframe.protocol.temp.packet.MinecraftPacketOutPong;
import eu.mshade.mwork.event.EventListener;

public class ServerPingListener implements EventListener<ServerPingEvent> {

    @Override
    public void onEvent(ServerPingEvent event) {
        SessionWrapper sessionWrapper = event.getSessionWrapper();
        sessionWrapper.sendPacket(new MinecraftPacketOutPong(event.getPayload()));
    }
}
