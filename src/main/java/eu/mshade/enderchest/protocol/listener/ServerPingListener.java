package eu.mshade.enderchest.listener;

import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.event.server.ServerPingEvent;
import eu.mshade.enderframe.protocol.packet.PacketOutPong;
import eu.mshade.mwork.dispatcher.DispatcherContainer;
import eu.mshade.mwork.dispatcher.DispatcherListener;

public class ServerPingListener implements DispatcherListener<ServerPingEvent> {

    @Override
    public void handle(ServerPingEvent serverPingEvent, DispatcherContainer dispatcherContainer) {
        EnderFrameSessionHandler bridge = dispatcherContainer.getContainer(EnderFrameSessionHandler.class);
        bridge.sendPacketAndClose(new PacketOutPong(serverPingEvent.getPayload()));
    }
}
