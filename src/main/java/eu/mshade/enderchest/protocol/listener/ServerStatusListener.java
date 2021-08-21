package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.redstone.Redstone;
import eu.mshade.enderchest.redstone.RedstoneSession;
import eu.mshade.enderchest.redstone.protocol.packet.RedstonePacketInMotd;
import eu.mshade.enderchest.redstone.protocol.packet.RedstonePacketOutMotd;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.event.server.ServerStatusEvent;
import eu.mshade.enderframe.mojang.chat.TextComponent;
import eu.mshade.enderframe.motd.MotdComponent;
import eu.mshade.enderframe.motd.MotdPlayer;
import eu.mshade.enderframe.motd.MotdVersion;
import eu.mshade.enderframe.protocol.temp.packet.PacketOutStatus;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

import java.util.concurrent.CompletableFuture;

public class ServerStatusListener implements EventListener<ServerStatusEvent> {

    private Redstone redstone;
    public ServerStatusListener(Redstone redstone) {
        this.redstone = redstone;
    }

    @Override
    public void onEvent(ServerStatusEvent event, ParameterContainer eventContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = eventContainer.getContainer(EnderFrameSessionHandler.class);

        RedstonePacketOutMotd redstonePacketOutMotd = new RedstonePacketOutMotd();
        MotdVersion motdVersion = new MotdVersion("1.8.X", 47);
        MotdPlayer motdPlayer = new MotdPlayer(20, 0);
        MotdComponent motdComponent = new MotdComponent(motdVersion, motdPlayer, TextComponent.of("Hello"));
        CompletableFuture<RedstonePacketInMotd> redstonePacketInMotdCompletableFuture = CompletableFuture.completedFuture(new RedstonePacketInMotd(motdComponent));


        for (RedstoneSession redstoneSession : redstone.getRedstoneSessions()) {
            System.out.println(redstoneSession);
            redstonePacketInMotdCompletableFuture = redstone.sendPacket(redstoneSession, redstonePacketOutMotd, RedstonePacketInMotd.class);
        }
        redstonePacketInMotdCompletableFuture.thenAccept(redstonePacketInMotd -> {
            System.out.println(redstonePacketInMotd);
            enderFrameSessionHandler.sendPacket(new PacketOutStatus(redstonePacketInMotd.getMotdComponent()));
        });

    }
}
