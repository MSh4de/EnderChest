package eu.mshade.enderchest.emerald.listener;

import eu.mshade.enderchest.emerald.Emerald;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import eu.mshade.shulker.ShulkerService;
import eu.mshade.enderchest.emerald.ShulkerServiceRepository;
import eu.mshade.shulker.packet.ShulkerPacketLeaveService;
import eu.mshade.shulker.protocol.ShulkerPacketContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShulkerPacketLeaveServiceListener implements EventListener<ShulkerPacketContainer<ShulkerPacketLeaveService>> {

    private Emerald emerald;
    private static Logger logger = LoggerFactory.getLogger(ShulkerPacketLeaveServiceListener.class);

    public ShulkerPacketLeaveServiceListener(Emerald emerald) {
        this.emerald = emerald;
    }

    @Override
    public void onEvent(ShulkerPacketContainer<ShulkerPacketLeaveService> event, ParameterContainer eventContainer) {
        ShulkerPacketLeaveService shulkerPacketLeaveService = event.getShulkerPacketPayload().getShulkerPacket();
        ShulkerServiceRepository shulkerServiceRepository = emerald.getShulkerServiceRepository();
        ShulkerService shulkerService = shulkerServiceRepository.getShulkerService(shulkerPacketLeaveService.getShulkerService());
        emerald.getShulkerServiceRepository().removeShulkerService(shulkerService);
        logger.info(String.format("service %s has bean leave", shulkerService.getName()));
    }
}
