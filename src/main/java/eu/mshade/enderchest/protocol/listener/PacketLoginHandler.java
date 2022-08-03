package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.mojang.GameProfile;
import eu.mshade.enderframe.packetevent.PacketFinallyJoinEvent;
import eu.mshade.enderframe.packetevent.PacketLoginEvent;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.enderframe.protocol.SessionWrapper;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import io.netty.channel.Channel;

public class PacketLoginHandler implements EventListener<PacketLoginEvent> {

    private EnderChest enderChest;

    public PacketLoginHandler(EnderChest enderChest) {
        this.enderChest = enderChest;
    }

    @Override
    public void onEvent(PacketLoginEvent event, ParameterContainer parameterContainer) {
        Channel channel = parameterContainer.getContainer(Channel.class);
        SessionWrapper sessionWrapper = ProtocolPipeline.get().getSessionWrapper(channel);
        sessionWrapper.setGameProfile(new GameProfile(event.getName()));
        //sessionWrapper.sendEncryption(enderChest.getMinecraftEncryption().getKeyPair().getPublic());
        EnderFrame.get().getPacketEventBus().publish(new PacketFinallyJoinEvent(), parameterContainer);
    }
}
