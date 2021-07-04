package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.event.entity.PacketClientSettingsEvent;
import eu.mshade.mwork.event.EventContainer;
import eu.mshade.mwork.event.EventListener;

public class PacketClientSettingsHandler implements EventListener<PacketClientSettingsEvent> {
    /*
    @Override
    public void handle(PacketClientSettingsEvent packetClientSettingsEvent, DispatcherContainer dispatcherContainer) {
        System.out.println(packetClientSettingsEvent);
        EnderFrameSessionHandler enderFrameSessionHandler = dispatcherContainer.getContainer(EnderFrameSessionHandler.class);

    }

     */

    @Override
    public void onEvent(PacketClientSettingsEvent event, EventContainer eventContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = eventContainer.getContainer(EnderFrameSessionHandler.class);
    }
}
