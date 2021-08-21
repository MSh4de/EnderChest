package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.event.server.ServerPingEvent;
import eu.mshade.enderframe.protocol.temp.packet.PacketOutPong;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

public class ServerPingListener implements EventListener<ServerPingEvent> {

    /*
    @Override
    public void handle(ServerPingEvent serverPingEvent, DispatcherContainer dispatcherContainer) {
        EnderFrameSessionHandler bridge = dispatcherContainer.getContainer(EnderFrameSessionHandler.class);
        bridge.sendPacketAndClose(new PacketOutPong(serverPingEvent.getPayload()));
    }

     */

    @Override
    public void onEvent(ServerPingEvent event, ParameterContainer eventContainer) {
        EnderFrameSessionHandler bridge = eventContainer.getContainer(EnderFrameSessionHandler.class);
        bridge.sendPacket(new PacketOutPong(event.getPayload()));
    }
}
