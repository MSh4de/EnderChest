package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.EnderFrameSession;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.event.entity.PacketLoginEvent;
import eu.mshade.enderframe.mojang.GameProfile;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

public class PacketLoginHandler implements EventListener<PacketLoginEvent> {

    private DedicatedEnderChest dedicatedEnderChest;

    public PacketLoginHandler(DedicatedEnderChest dedicatedEnderChest) {
        this.dedicatedEnderChest = dedicatedEnderChest;
    }

    @Override
    public void onEvent(PacketLoginEvent event, ParameterContainer eventContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = eventContainer.getContainer(EnderFrameSessionHandler.class);
        EnderFrameSession enderFrameSession = enderFrameSessionHandler.getEnderFrameProtocol().getEnderFrameSession(enderFrameSessionHandler);
        enderFrameSessionHandler.setEnderFrameSession(enderFrameSession);
        enderFrameSession.setGameProfile(new GameProfile(event.getName()));
        enderFrameSession.sendEncryption(dedicatedEnderChest.getMinecraftEncryption().getKeyPair().getPublic());
    }
}
