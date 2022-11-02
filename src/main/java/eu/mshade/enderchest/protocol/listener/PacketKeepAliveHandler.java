package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderframe.PlayerInfoBuilder;
import eu.mshade.enderframe.PlayerInfoType;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.packetevent.PacketKeepAliveEvent;
import eu.mshade.enderframe.protocol.SessionWrapper;
import eu.mshade.mwork.event.EventListener;

public class PacketKeepAliveHandler implements EventListener<PacketKeepAliveEvent> {

    private EnderChest enderChest;

    public PacketKeepAliveHandler(EnderChest enderChest) {
        this.enderChest = enderChest;
    }

    @Override
    public void onEvent(PacketKeepAliveEvent event) {
        SessionWrapper sessionWrapper = event.getSessionWrapper();
        Player player = sessionWrapper.getPlayer();

        int ping = (int) (System.currentTimeMillis() - event.getThreshold());
        player.setPing(ping);
        PlayerInfoBuilder playerInfoBuilder = PlayerInfoBuilder
                .of(PlayerInfoType.UPDATE_LATENCY);
        enderChest.getPlayers().forEach(playerInfoBuilder::withPlayer);
        sessionWrapper.sendPlayerInfo(playerInfoBuilder);
    }
}
