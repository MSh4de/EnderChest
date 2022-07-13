package eu.mshade.enderchest.protocol.listener;

import com.google.inject.Inject;
import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderframe.PlayerInfoBuilder;
import eu.mshade.enderframe.PlayerInfoType;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.packetevent.PacketKeepAliveEvent;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.enderframe.protocol.SessionWrapper;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import io.netty.channel.Channel;

public class PacketKeepAliveHandler implements EventListener<PacketKeepAliveEvent> {

    @Inject
    private EnderChest enderChest;
    @Override
    public void onEvent(PacketKeepAliveEvent event, ParameterContainer parameterContainer) {
        Channel channel = parameterContainer.getContainer(Channel.class);
        Player player = ProtocolPipeline.get().getPlayer(channel);
        SessionWrapper sessionWrapper = player.getSessionWrapper();

        int ping = (int) (System.currentTimeMillis() - event.getThreshold());
        player.setPing(ping);
        PlayerInfoBuilder playerInfoBuilder = PlayerInfoBuilder
                .of(PlayerInfoType.UPDATE_LATENCY);
        enderChest.getPlayers().forEach(playerInfoBuilder::withPlayer);
        sessionWrapper.sendPlayerInfo(playerInfoBuilder);
    }
}
