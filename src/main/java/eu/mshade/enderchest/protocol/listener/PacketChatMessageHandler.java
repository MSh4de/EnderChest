package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderframe.entity.ArmorStand;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.entity.Zombie;
import eu.mshade.enderframe.mojang.chat.ChatColor;
import eu.mshade.enderframe.packetevent.PacketChatMessageEvent;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import io.netty.channel.Channel;

public class PacketChatMessageHandler implements EventListener<PacketChatMessageEvent> {


    private EnderChest enderChest;

    public PacketChatMessageHandler(EnderChest enderChest) {
        this.enderChest = enderChest;
    }

    @Override
    public void onEvent(PacketChatMessageEvent event, ParameterContainer parameterContainer) {
        ProtocolPipeline protocolPipeline = ProtocolPipeline.get();
        Channel channel = parameterContainer.getContainer(Channel.class);
        Player player = protocolPipeline.getPlayer(channel);

        if (event.getMessage().startsWith("entity")){
            ArmorStand blaze = new ArmorStand(player.getLocation().clone(), 100) {

                @Override
                public void tick() {

                }
            };

            player.getSessionWrapper().sendEntity(blaze);
            return;
        }

        enderChest.getPlayers().forEach(each -> each.getSessionWrapper().sendMessage(player.getDisplayName()+" : "+ChatColor.translateAlternateColorCodes('&',event.getMessage())));
        System.out.println(player.getDisplayName()+" : "+event.getMessage());
    }
}
