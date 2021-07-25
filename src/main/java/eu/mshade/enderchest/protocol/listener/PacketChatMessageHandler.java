package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.EnderFrameSession;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.event.entity.PacketChatMessageEvent;
import eu.mshade.enderframe.mojang.chat.ChatColor;
import eu.mshade.enderframe.mojang.chat.TextComponent;
import eu.mshade.enderframe.mojang.chat.TextPosition;
import eu.mshade.enderman.packet.play.PacketOutChatMessage;
import eu.mshade.enderman.packet.play.PacketOutSpawnMob;
import eu.mshade.mwork.event.EventContainer;
import eu.mshade.mwork.event.EventListener;

public class PacketChatMessageHandler implements EventListener<PacketChatMessageEvent> {

    private DedicatedEnderChest dedicatedEnderChest;

    public PacketChatMessageHandler(DedicatedEnderChest dedicatedEnderChest) {
        this.dedicatedEnderChest = dedicatedEnderChest;
    }


    @Override
    public void onEvent(PacketChatMessageEvent event, EventContainer eventContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = eventContainer.getContainer(EnderFrameSessionHandler.class);
        String displayName = enderFrameSessionHandler.getEnderFrameSession().getGameProfile().getName();
        if(event.getMessage().startsWith("/"))return;
        String[] args = event.getMessage().split(" ");

        if(args[0].equalsIgnoreCase("!spawn")){
            System.out.println("SPAWN ENTITY");
            enderFrameSessionHandler.sendPacket(new PacketOutSpawnMob(Integer.parseInt(args[1]), Integer.parseInt(args[2]), (int)enderFrameSessionHandler.getEnderFrameSession().getLocation().getX()+2, (int) enderFrameSessionHandler.getEnderFrameSession().getLocation().getY(), (int) enderFrameSessionHandler.getEnderFrameSession().getLocation().getZ()+2));
        }
        dedicatedEnderChest.getEnderFrameSessions().forEach(each -> each.sendMessage(displayName+" : "+ChatColor.translateAlternateColorCodes('&',event.getMessage())));
        System.out.println(displayName+" : "+event.getMessage());
    }
}
