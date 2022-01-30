package eu.mshade.enderchest.emerald.listener;

import eu.mshade.enderchest.emerald.Emerald;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import eu.mshade.shulker.ShulkerService;
import eu.mshade.shulker.packet.ShulkerPacketJoinService;
import eu.mshade.shulker.protocol.ShulkerPacketContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShulkerPacketJoinServiceListener implements EventListener<ShulkerPacketContainer<ShulkerPacketJoinService>> {

    private Emerald emerald;
    private static Logger logger = LoggerFactory.getLogger(ShulkerPacketJoinServiceListener.class);

    public ShulkerPacketJoinServiceListener(Emerald emerald) {
        this.emerald = emerald;
    }

    @Override
    public void onEvent(ShulkerPacketContainer<ShulkerPacketJoinService> event, ParameterContainer eventContainer) {
        ShulkerPacketJoinService shulkerPacket = event.getShulkerPacketPayload().getShulkerPacket();
        ShulkerService shulkerService = shulkerPacket.getShulkerService();

        logger.info(String.format("new service add into server(%s)", shulkerService.getName()));
        emerald.getShulkerServiceRepository().addShulkerService(shulkerService);
    }
}
