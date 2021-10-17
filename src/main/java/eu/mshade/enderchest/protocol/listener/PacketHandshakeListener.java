package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.event.entity.PacketHandshakeEvent;
import eu.mshade.enderframe.mojang.chat.TextComponent;
import eu.mshade.enderframe.protocol.Handshake;
import eu.mshade.enderframe.protocol.HandshakeStatus;
import eu.mshade.enderframe.protocol.ProtocolStatus;
import eu.mshade.enderframe.protocol.ProtocolVersion;
import eu.mshade.enderframe.protocol.packet.PacketOutDisconnect;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

public class PacketHandshakeListener implements EventListener<PacketHandshakeEvent> {

    private final DedicatedEnderChest dedicatedEnderChest;

    public PacketHandshakeListener(DedicatedEnderChest dedicatedEnderChest) {
        this.dedicatedEnderChest = dedicatedEnderChest;
    }

    @Override
    public void onEvent(PacketHandshakeEvent event, ParameterContainer eventContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = eventContainer.getContainer(EnderFrameSessionHandler.class);
        Handshake handshake = event.getHandshake();
        HandshakeStatus handshakeStatus = handshake.getHandshakeStatus();
        ProtocolVersion protocolVersion = handshake.getVersion();

        enderFrameSessionHandler.setHandshake(handshake);
        if (handshakeStatus == HandshakeStatus.STATUS){
            enderFrameSessionHandler.toggleProtocolStatus(ProtocolStatus.STATUS);
        }else {
            enderFrameSessionHandler.toggleProtocolStatus(ProtocolStatus.LOGIN);
            if (protocolVersion != ProtocolVersion.UNKNOWN) {
                dedicatedEnderChest.getProtocolRepository().getProtocolFrameByVersion(protocolVersion)
                        .ifPresent(enderFrameSessionHandler::toggleEnderFrameProtocol)
                        .ifNotPresent(unused -> {
                            enderFrameSessionHandler.sendPacketAndClose(new PacketOutDisconnect(TextComponent.of("You version has not supported by MShade")));
                        });
            } else {
                enderFrameSessionHandler.sendPacketAndClose(new PacketOutDisconnect(TextComponent.of("You version has not supported by MShade")));
            }
        }
    }
}
