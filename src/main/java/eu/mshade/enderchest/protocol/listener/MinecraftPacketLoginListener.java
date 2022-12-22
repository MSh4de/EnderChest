package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderframe.mojang.GameProfile;
import eu.mshade.enderframe.packetevent.MinecraftPacketLoginEvent;
import eu.mshade.enderframe.protocol.MinecraftSession;
import eu.mshade.mwork.event.EventListener;

public class MinecraftPacketLoginListener implements EventListener<MinecraftPacketLoginEvent> {

    private EnderChest enderChest;

    public MinecraftPacketLoginListener(EnderChest enderChest) {
        this.enderChest = enderChest;
    }

    @Override
    public void onEvent(MinecraftPacketLoginEvent event) {
        MinecraftSession minecraftSession = event.getMinecraftSession();
        minecraftSession.setGameProfile(new GameProfile(event.getName()));
        minecraftSession.sendEncryption(enderChest.getMinecraftEncryption().getKeyPair().getPublic());
        //EnderFrame.get().getPacketEventBus().publish(new PacketFinallyJoinEvent(minecraftSession));
    }
}
