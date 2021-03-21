package eu.mshadeproduction.enderchest.listener;

import eu.mshadeproduction.enderframe.EnderFrameBridge;
import eu.mshadeproduction.enderframe.MotdBuilder;
import eu.mshadeproduction.enderframe.event.server.ServerStatusEvent;
import eu.mshadeproduction.enderframe.protocol.packet.EnderFramePacketOutStatus;
import eu.mshadeproduction.mwork.dispatcher.DispatcherContainer;
import eu.mshadeproduction.mwork.dispatcher.DispatcherListener;

public class ServerStatusListener implements DispatcherListener<ServerStatusEvent> {

    @Override
    public void handle(ServerStatusEvent serverStatusEvent, DispatcherContainer dispatcherContainer) {
        EnderFrameBridge bridge = dispatcherContainer.getContainer(EnderFrameBridge.class);
        MotdBuilder builder = MotdBuilder.builder()
                .setPlayer(20, 0)
                .setVersion("1.8.8", 47)
                .setDescription("EnderChest");

        bridge.sendPacket(new EnderFramePacketOutStatus(builder));
    }
}
