package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.mojang.chat.TextComponent;
import eu.mshade.enderframe.motd.MotdComponent;
import eu.mshade.enderframe.motd.MotdPlayer;
import eu.mshade.enderframe.motd.MotdVersion;
import eu.mshade.enderframe.packetevent.ServerStatusEvent;
import eu.mshade.enderframe.protocol.temp.packet.PacketOutStatus;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

public class ServerStatusListener implements EventListener<ServerStatusEvent> {

    @Override
    public void onEvent(ServerStatusEvent event, ParameterContainer eventContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = event.getEnderFrameSessionHandler();

        MotdVersion motdVersion = new MotdVersion("1.8.X", 47);
        MotdPlayer motdPlayer = new MotdPlayer(20, 0);
        MotdComponent motdComponent = new MotdComponent(motdVersion, motdPlayer, TextComponent.of("Hello"));


        enderFrameSessionHandler.sendPacket(new PacketOutStatus(motdComponent));

    }
}
