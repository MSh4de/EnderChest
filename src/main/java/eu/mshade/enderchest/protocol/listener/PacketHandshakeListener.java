package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.packetevent.PacketHandshakeEvent;
import eu.mshade.enderframe.mojang.chat.TextComponent;
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
    /*
    @Override
    public void handle(PacketHandshakeEvent packetHandshakeEvent, DispatcherContainer dispatcherContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = dispatcherContainer.getContainer(EnderFrameSessionHandler.class);
        HandshakeStatus handshakeStatus = packetHandshakeEvent.getHandshake().getHandshakeStatus();
        ProtocolVersion protocolVersion = packetHandshakeEvent.getHandshake().getVersion();
        if (handshakeStatus == HandshakeStatus.STATUS){
            enderFrameSessionHandler.toggleProtocolStatus(ProtocolStatus.STATUS);
        }else {
            enderFrameSessionHandler.toggleProtocolStatus(ProtocolStatus.LOGIN);
            if (protocolVersion != ProtocolVersion.UNKNOWN) {
                enderFrameSessionHandler.setProtocolVersion(protocolVersion);
                dedicatedEnderChest.getProtocolRepository().getProtocolFrameByVersion(protocolVersion)
                        .ifPresent(enderFrameSessionHandler::toggleEnderFrameProtocol)
                        .ifNotPresent(unused -> {
                            enderFrameSessionHandler.sendPacketAndClose(new PacketOutDisconnect(new TextComponent("You version has not supported by MShade")));
                        });
            }else {
                enderFrameSessionHandler.sendPacketAndClose(new PacketOutDisconnect(new TextComponent("You version has not supported by MShade")));
            }
        }
    }

     */

    @Override
    public void onEvent(PacketHandshakeEvent event, ParameterContainer eventContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = eventContainer.getContainer(EnderFrameSessionHandler.class);
        HandshakeStatus handshakeStatus = event.getHandshake().getHandshakeStatus();
        ProtocolVersion protocolVersion = event.getHandshake().getVersion();
        if (handshakeStatus == HandshakeStatus.STATUS){
            enderFrameSessionHandler.toggleProtocolStatus(ProtocolStatus.STATUS);
        }else {
            enderFrameSessionHandler.toggleProtocolStatus(ProtocolStatus.LOGIN);
            if (protocolVersion != ProtocolVersion.UNKNOWN) {
                enderFrameSessionHandler.setProtocolVersion(protocolVersion);
                dedicatedEnderChest.getProtocolRepository().getProtocolFrameByVersion(protocolVersion)
                        .ifPresent(enderFrameSessionHandler::toggleEnderFrameProtocol)
                        .ifNotPresent(unused -> {
                            enderFrameSessionHandler.sendPacketAndClose(new PacketOutDisconnect(TextComponent.of("You version has not supported by MShade")));
                        });
            }else {
                enderFrameSessionHandler.sendPacketAndClose(new PacketOutDisconnect(TextComponent.of("You version has not supported by MShade")));
            }
        }
    }
}
