package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.packetevent.ServerPingEvent;
import eu.mshade.enderframe.protocol.temp.packet.PacketOutPong;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

public class ServerPingListener implements EventListener<ServerPingEvent> {

    @Override
    public void onEvent(ServerPingEvent event, ParameterContainer eventContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = event.getEnderFrameSessionHandler();
        enderFrameSessionHandler.sendPacket(new PacketOutPong(event.getPayload()));
    }
}
