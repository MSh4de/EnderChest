package eu.mshade.enderchest.emerald.listener;

import eu.mshade.enderchest.emerald.Emerald;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import eu.mshade.shulker.packet.ShulkerPacketLoginResponse;
import eu.mshade.shulker.protocol.ShulkerPacket;
import eu.mshade.shulker.protocol.ShulkerPacketContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShulkerPacketLoginResponseListener implements EventListener<ShulkerPacketContainer<ShulkerPacketLoginResponse>> {

    private Emerald emerald;
    private static Logger logger = LoggerFactory.getLogger(ShulkerPacketLoginResponseListener.class);

    public ShulkerPacketLoginResponseListener(Emerald emerald) {
        this.emerald = emerald;
    }

    @Override
    public void onEvent(ShulkerPacketContainer<ShulkerPacketLoginResponse> event, ParameterContainer eventContainer) {
        ShulkerPacketLoginResponse shulkerPacketLoginResponse = event.getShulkerPacketPayload().getShulkerPacket();
        logger.info(shulkerPacketLoginResponse.getShulkerService().toString());
        this.emerald.setShulkerService(shulkerPacketLoginResponse.getShulkerService());
    }

}
