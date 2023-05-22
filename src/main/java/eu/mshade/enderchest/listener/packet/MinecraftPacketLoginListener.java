package eu.mshade.enderchest.listener.packet;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.event.PrePlayerJoinEvent;
import eu.mshade.enderframe.mojang.GameProfile;
import eu.mshade.enderframe.packetevent.MinecraftPacketLoginEvent;
import eu.mshade.enderframe.protocol.MinecraftSession;
import eu.mshade.mwork.event.EventListener;

public class MinecraftPacketLoginListener implements EventListener<MinecraftPacketLoginEvent> {

    @Override
    public void onEvent(MinecraftPacketLoginEvent event) {
        MinecraftSession minecraftSession = event.getMinecraftSession();
        minecraftSession.gameProfile = new GameProfile(event.getName());
//        minecraftSession.sendEncryption(EnderChest.INSTANCE.getMinecraftEncryption().getKeyPair().getPublic());
        EnderFrame.get().getMinecraftEvents().publish(new PrePlayerJoinEvent(minecraftSession));
    }
}
