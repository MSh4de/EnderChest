package eu.mshade.enderchest.emerald.listener;

import eu.mshade.enderchest.emerald.Emerald;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import eu.mshade.shulker.packet.ShulkerPacketJoinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShulkerPacketJoinServiceListener implements EventListener<ShulkerPacketJoinService> {

    private Emerald emerald;
    private static Logger logger = LoggerFactory.getLogger(ShulkerPacketJoinServiceListener.class);

    public ShulkerPacketJoinServiceListener(Emerald emerald) {
        this.emerald = emerald;
    }

    @Override
    public void onEvent(ShulkerPacketJoinService event, ParameterContainer eventContainer) {
        logger.info(String.format("new service add into server(%s)", event.getShulkerService().getName()));
        emerald.getShulkerServiceRepository().addShulkerService(event.getShulkerService());
    }
}
