package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.protocol.ProtocolRepository;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.event.entity.PacketHandshakeEvent;
import eu.mshade.enderframe.protocol.HandshakeStatus;
import eu.mshade.enderframe.protocol.ProtocolStatus;
import eu.mshade.enderframe.protocol.ProtocolVersion;
import eu.mshade.mwork.dispatcher.DispatcherContainer;
import eu.mshade.mwork.dispatcher.DispatcherListener;

public class HandshakeListener implements DispatcherListener<PacketHandshakeEvent> {

    private ProtocolRepository protocolRepository;
    public HandshakeListener(ProtocolRepository protocolRepository) {
        this.protocolRepository = protocolRepository;
    }

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
                protocolRepository.getProtocolFrameByVersion(protocolVersion).ifPresent(enderFrameSessionHandler::toggleEnderFrameProtocol);
            }
        }
    }
}
