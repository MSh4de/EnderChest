package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.EnderFrameSession;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.PlayerInfoBuilder;
import eu.mshade.enderframe.PlayerInfoType;
import eu.mshade.enderframe.event.entity.PacketKeepAliveEvent;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

public class PacketKeepAliveHandler implements EventListener<PacketKeepAliveEvent> {

    private DedicatedEnderChest dedicatedEnderChest;

    public PacketKeepAliveHandler(DedicatedEnderChest dedicatedEnderChest) {
        this.dedicatedEnderChest = dedicatedEnderChest;
    }
    /*
    @Override
    public void handle(PacketKeepAliveEvent packetKeepAliveEvent, DispatcherContainer dispatcherContainer) {
        EnderFrameSession enderFrameSession = dispatcherContainer.getContainer(EnderFrameSessionHandler.class).getEnderFrameSession();
        int ping = (int) (System.currentTimeMillis() - packetKeepAliveEvent.getThreshold());
        enderFrameSession.setPing(ping);
        PlayerInfoBuilder playerInfoBuilder = PlayerInfoBuilder
                .of(PlayerInfoType.UPDATE_LATENCY);
        dedicatedEnderChest.getEnderFrameSessions().forEach(playerInfoBuilder::withPlayer);
        enderFrameSession.sendPlayerInfo(playerInfoBuilder);
        //System.out.println(String.format("%s %d", enderFrameSession.getGameProfile().getName(), ping));
    }

     */

    @Override
    public void onEvent(PacketKeepAliveEvent event, ParameterContainer eventContainer) {
        EnderFrameSession enderFrameSession = eventContainer.getContainer(EnderFrameSessionHandler.class).getEnderFrameSession();
        int ping = (int) (System.currentTimeMillis() - event.getThreshold());
        enderFrameSession.getPlayer().setPing(ping);
        PlayerInfoBuilder playerInfoBuilder = PlayerInfoBuilder
                .of(PlayerInfoType.UPDATE_LATENCY);
        dedicatedEnderChest.getEnderFrameSessions().forEach(playerInfoBuilder::withPlayer);
        enderFrameSession.sendPlayerInfo(playerInfoBuilder);
    }
}
