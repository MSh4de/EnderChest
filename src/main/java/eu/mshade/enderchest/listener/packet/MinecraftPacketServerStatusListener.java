package eu.mshade.enderchest.listener.packet;

import eu.mshade.enderframe.mojang.chat.TextComponent;
import eu.mshade.enderframe.motd.MotdComponent;
import eu.mshade.enderframe.motd.MotdPlayer;
import eu.mshade.enderframe.motd.MotdVersion;
import eu.mshade.enderframe.packetevent.MinecraftPacketServerStatusEvent;
import eu.mshade.enderframe.protocol.MinecraftSession;
import eu.mshade.enderframe.protocol.temp.packet.MinecraftPacketOutServerStatus;
import eu.mshade.mwork.event.EventListener;

public class MinecraftPacketServerStatusListener implements EventListener<MinecraftPacketServerStatusEvent> {

    @Override
    public void onEvent(MinecraftPacketServerStatusEvent event) {
        MinecraftSession minecraftSession = event.getMinecraftSession();

        MotdVersion motdVersion = new MotdVersion("1.8.X", 47);
        MotdPlayer motdPlayer = new MotdPlayer(20, 0);
        MotdComponent motdComponent = new MotdComponent(motdVersion, motdPlayer, TextComponent.of("Hello"));


        minecraftSession.sendPacket(new MinecraftPacketOutServerStatus(motdComponent));

    }
}
