package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.packetevent.PacketCloseInventoryEvent;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

public class PacketCloseInventoryHandler implements EventListener<PacketCloseInventoryEvent> {

    @Override
    public void onEvent(PacketCloseInventoryEvent event, ParameterContainer eventContainer) {
        System.out.println(event);
    }
}
