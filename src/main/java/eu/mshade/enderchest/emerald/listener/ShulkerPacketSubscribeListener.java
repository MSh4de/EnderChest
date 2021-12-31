package eu.mshade.enderchest.emerald.listener;

import eu.mshade.enderchest.emerald.Emerald;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import eu.mshade.shulker.ShulkerService;
import eu.mshade.shulker.packet.ShulkerPacketSubscribe;
import eu.mshade.shulker.protocol.ShulkerPacketContainer;
import eu.mshade.shulker.protocol.ShulkerPacketPayload;
import eu.mshade.shulker.protocol.ShulkerPacketType;

public class ShulkerPacketSubscribeListener implements EventListener<ShulkerPacketContainer<ShulkerPacketSubscribe>> {

    private Emerald emerald;

    public ShulkerPacketSubscribeListener(Emerald emerald) {
        this.emerald = emerald;
    }

    @Override
    public void onEvent(ShulkerPacketContainer<ShulkerPacketSubscribe> event, ParameterContainer eventContainer) {
        ShulkerPacketPayload<ShulkerPacketSubscribe> shulkerPacketPayload = event.getShulkerPacketPayload();
        ShulkerPacketSubscribe shulkerPacketSubscribe = shulkerPacketPayload.getShulkerPacket();

        long author = event.getShulkerPacketHeader().getAuthor();
        ShulkerService shulkerService = emerald.getShulkerServiceRepository().getShulkerService(author);
        if (shulkerService == null) return;

        ShulkerPacketType shulkerPacketType = shulkerPacketSubscribe.getShulkerPacketType();
        emerald.getShulkerServiceRepository().addSubscriber(shulkerService, shulkerPacketType);
    }
}
