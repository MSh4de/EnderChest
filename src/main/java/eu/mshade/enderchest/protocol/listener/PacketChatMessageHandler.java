package eu.mshade.enderchest.protocol.listener;

import com.google.inject.Inject;
import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.mojang.chat.ChatColor;
import eu.mshade.enderframe.packetevent.PacketChatMessageEvent;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import io.netty.channel.Channel;

public class PacketChatMessageHandler implements EventListener<PacketChatMessageEvent> {

    @Inject
    private EnderChest enderChest;

    @Override
    public void onEvent(PacketChatMessageEvent event, ParameterContainer parameterContainer) {
        ProtocolPipeline protocolPipeline = ProtocolPipeline.get();
        Channel channel = parameterContainer.getContainer(Channel.class);
        Player player = protocolPipeline.getPlayer(channel);

        enderChest.getPlayers().forEach(each -> each.getSessionWrapper().sendMessage(player.getDisplayName()+" : "+ChatColor.translateAlternateColorCodes('&',event.getMessage())));
        System.out.println(player.getDisplayName()+" : "+event.getMessage());
    }
}
