package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.event.entity.PacketChatMessageEvent;
import eu.mshade.mwork.event.EventContainer;
import eu.mshade.mwork.event.EventListener;

public class PacketChatMessageHandler implements EventListener<PacketChatMessageEvent> {

    private DedicatedEnderChest dedicatedEnderChest;

    public PacketChatMessageHandler(DedicatedEnderChest dedicatedEnderChest) {
        this.dedicatedEnderChest = dedicatedEnderChest;
    }


    @Override
    public void onEvent(PacketChatMessageEvent event, EventContainer eventContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = eventContainer.getContainer(EnderFrameSessionHandler.class);
        System.out.println(event.getMessage());
    }
}
