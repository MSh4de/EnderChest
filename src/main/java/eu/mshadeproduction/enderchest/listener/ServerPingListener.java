package eu.mshadeproduction.enderchest.listener;

import eu.mshadeproduction.enderframe.EnderFrameBridge;
import eu.mshadeproduction.enderframe.event.server.ServerPingEvent;
import eu.mshadeproduction.enderframe.protocol.packet.EnderFramePacketOutPong;
import eu.mshadeproduction.mwork.dispatcher.DispatcherContainer;
import eu.mshadeproduction.mwork.dispatcher.DispatcherListener;

public class ServerPingListener implements DispatcherListener<ServerPingEvent> {

    @Override
    public void handle(ServerPingEvent serverPingEvent, DispatcherContainer dispatcherContainer) {
        EnderFrameBridge bridge = dispatcherContainer.getContainer(EnderFrameBridge.class);
        bridge.sendPacketAndClose(new EnderFramePacketOutPong(serverPingEvent.getPayload()));
    }
}
