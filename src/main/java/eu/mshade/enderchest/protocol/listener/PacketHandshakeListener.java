package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderframe.mojang.chat.TextComponent;
import eu.mshade.enderframe.packetevent.PacketHandshakeEvent;
import eu.mshade.enderframe.protocol.*;
import eu.mshade.enderframe.protocol.packet.PacketOutDisconnect;
import eu.mshade.mwork.MOptional;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import io.netty.channel.Channel;


public class PacketHandshakeListener implements EventListener<PacketHandshakeEvent> {


    private EnderChest enderChest;

    public PacketHandshakeListener(EnderChest enderChest) {
        this.enderChest = enderChest;
    }

    @Override
    public void onEvent(PacketHandshakeEvent event, ParameterContainer parameterContainer) {
        ProtocolPipeline protocolPipeline = ProtocolPipeline.get();
        Channel channel = parameterContainer.getContainer(Channel.class);
        SessionWrapper sessionWrapper = protocolPipeline.getSessionWrapper(channel);
        Handshake handshake = event.getHandshake();
        HandshakeStatus handshakeStatus = handshake.getHandshakeStatus();
        MinecraftProtocolVersion minecraftProtocolVersion = handshake.getVersion();

        if (handshakeStatus == HandshakeStatus.STATUS){
            sessionWrapper.toggleProtocolStatus(ProtocolStatus.STATUS);
        }else {
            sessionWrapper.toggleProtocolStatus(ProtocolStatus.LOGIN);
            if (minecraftProtocolVersion != MinecraftProtocolVersion.UNKNOWN) {
                MOptional<Protocol> protocolFrameByVersion = enderChest.getProtocolRepository().getProtocolFrameByVersion(minecraftProtocolVersion);
                if (protocolFrameByVersion.isPresent()) {
                    Protocol protocol = protocolFrameByVersion.get();
                    sessionWrapper = protocol.getSessionWrapper(channel);
                    protocolPipeline.setSessionWrapper(channel, sessionWrapper);
                    sessionWrapper.toggleProtocol(protocol);
                    sessionWrapper.toggleProtocolStatus(ProtocolStatus.LOGIN);
                }else {
                    sessionWrapper.sendPacketAndClose(new PacketOutDisconnect(TextComponent.of("Your version is not supported by MShade")));
                }
            } else {
                sessionWrapper.sendPacketAndClose(new PacketOutDisconnect(TextComponent.of("Your version is not supported by MShade")));
            }
        }
        sessionWrapper.setHandshake(handshake);
    }
}
