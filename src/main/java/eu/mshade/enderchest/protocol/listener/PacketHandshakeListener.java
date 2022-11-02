package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderframe.mojang.chat.TextComponent;
import eu.mshade.enderframe.packetevent.PacketHandshakeEvent;
import eu.mshade.enderframe.protocol.*;
import eu.mshade.enderframe.protocol.packet.MinecraftPacketOutDisconnect;
import eu.mshade.mwork.event.EventListener;
import io.netty.channel.Channel;


public class PacketHandshakeListener implements EventListener<PacketHandshakeEvent> {


    private EnderChest enderChest;

    public PacketHandshakeListener(EnderChest enderChest) {
        this.enderChest = enderChest;
    }

    @Override
    public void onEvent(PacketHandshakeEvent event) {
        ProtocolPipeline protocolPipeline = ProtocolPipeline.get();
        SessionWrapper sessionWrapper = event.getSessionWrapper();
        Channel channel = sessionWrapper.getChannel();
        Handshake handshake = event.getHandshake();
        HandshakeStatus handshakeStatus = handshake.getHandshakeStatus();
        MinecraftProtocolVersion minecraftProtocolVersion = handshake.getVersion();

        if (handshakeStatus == HandshakeStatus.STATUS){
            sessionWrapper.toggleProtocolStatus(ProtocolStatus.STATUS);
        }else {
            sessionWrapper.toggleProtocolStatus(ProtocolStatus.LOGIN);
            if (minecraftProtocolVersion != MinecraftProtocolVersion.UNKNOWN) {
                Protocol protocol = enderChest.getProtocolRepository().getProtocolFrameByVersion(minecraftProtocolVersion);
                if (protocol != null) {
                    sessionWrapper = protocol.getSessionWrapper(channel);
                    protocolPipeline.setSessionWrapper(channel, sessionWrapper);
                    sessionWrapper.toggleProtocol(protocol);
                    sessionWrapper.toggleProtocolStatus(ProtocolStatus.LOGIN);
                }else {
                    sessionWrapper.sendPacketAndClose(new MinecraftPacketOutDisconnect(TextComponent.of("Your version is not supported by MShade")));
                }
            } else {
                sessionWrapper.sendPacketAndClose(new MinecraftPacketOutDisconnect(TextComponent.of("Your version is not supported by MShade")));
            }
        }
        sessionWrapper.setHandshake(handshake);
    }
}
