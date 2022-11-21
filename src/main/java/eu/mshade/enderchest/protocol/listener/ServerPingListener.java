package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.packetevent.ServerPingEventMinecraft;
import eu.mshade.enderframe.protocol.MinecraftSession;
import eu.mshade.enderframe.protocol.temp.packet.MinecraftPacketOutPong;
import eu.mshade.mwork.event.EventListener;

public class ServerPingListener implements EventListener<ServerPingEventMinecraft> {

    @Override
    public void onEvent(ServerPingEventMinecraft event) {
        MinecraftSession minecraftSession = event.getSessionWrapper();
        minecraftSession.sendPacket(new MinecraftPacketOutPong(event.getPayload()));
    }
}
