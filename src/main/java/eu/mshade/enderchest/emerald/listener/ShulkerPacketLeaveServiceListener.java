package eu.mshade.enderchest.emerald.listener;

import eu.mshade.enderchest.emerald.Emerald;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import eu.mshade.shulker.ShulkerService;
import eu.mshade.enderchest.emerald.ShulkerServiceRepository;
import eu.mshade.shulker.packet.ShulkerPacketLeaveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShulkerPacketLeaveServiceListener implements EventListener<ShulkerPacketLeaveService> {

    private Emerald emerald;
    private static Logger logger = LoggerFactory.getLogger(ShulkerPacketLeaveServiceListener.class);

    public ShulkerPacketLeaveServiceListener(Emerald emerald) {
        this.emerald = emerald;
    }

    @Override
    public void onEvent(ShulkerPacketLeaveService event, ParameterContainer eventContainer) {
        ShulkerServiceRepository shulkerServiceRepository = emerald.getShulkerServiceRepository();
        ShulkerService shulkerService = shulkerServiceRepository.getShulkerService(event.getShulkerService());
        emerald.getShulkerServiceRepository().removeShulkerService(event.getShulkerService());
        logger.info(String.format("service %s has bean leave", shulkerService.getName()));
    }
}
