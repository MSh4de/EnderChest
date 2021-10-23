package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.EnderFrameSession;
import eu.mshade.enderframe.PlayerInfoBuilder;
import eu.mshade.enderframe.PlayerInfoType;
import eu.mshade.enderframe.packetevent.PacketKeepAliveEvent;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

public class PacketKeepAliveHandler implements EventListener<PacketKeepAliveEvent> {

    private DedicatedEnderChest dedicatedEnderChest;

    public PacketKeepAliveHandler(DedicatedEnderChest dedicatedEnderChest) {
        this.dedicatedEnderChest = dedicatedEnderChest;
    }

    @Override
    public void onEvent(PacketKeepAliveEvent event, ParameterContainer eventContainer) {
        EnderFrameSession enderFrameSession = event.getPlayer().getEnderFrameSessionHandler().getEnderFrameSession();
        int ping = (int) (System.currentTimeMillis() - event.getThreshold());
        enderFrameSession.getPlayer().setPing(ping);
        PlayerInfoBuilder playerInfoBuilder = PlayerInfoBuilder
                .of(PlayerInfoType.UPDATE_LATENCY);
        dedicatedEnderChest.getPlayers().forEach(playerInfoBuilder::withPlayer);
        enderFrameSession.sendPlayerInfo(playerInfoBuilder);
    }
}
