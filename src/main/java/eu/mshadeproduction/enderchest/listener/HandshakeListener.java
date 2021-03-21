package eu.mshadeproduction.enderchest.listener;

import eu.mshadeproduction.enderframe.EnderFrameBridge;
import eu.mshadeproduction.enderframe.event.player.HandshakeEvent;
import eu.mshadeproduction.enderframe.protocol.ProtocolStatus;
import eu.mshadeproduction.mwork.dispatcher.DispatcherContainer;
import eu.mshadeproduction.mwork.dispatcher.DispatcherListener;

public class HandshakeListener implements DispatcherListener<HandshakeEvent> {
    @Override
    public void handle(HandshakeEvent handshakeEvent, DispatcherContainer dispatcherContainer) {
        EnderFrameBridge bridge = dispatcherContainer.getContainer(EnderFrameBridge.class);
        bridge.toggleProtocolStatus(ProtocolStatus.STATUS);
    }
}
