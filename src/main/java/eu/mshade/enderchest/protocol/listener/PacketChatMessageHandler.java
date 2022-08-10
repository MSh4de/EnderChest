package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderchest.world.SchematicLoader;
import eu.mshade.enderframe.entity.ArmorStand;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.entity.Zombie;
import eu.mshade.enderframe.mojang.chat.ChatColor;
import eu.mshade.enderframe.packetevent.PacketChatMessageEvent;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import io.netty.channel.Channel;

import java.util.concurrent.ForkJoinPool;

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
        Location location = player.getLocation();

        if (event.getMessage().startsWith("schematic")){
            String[] args = event.getMessage().split(" ");
            if (args.length == 2){
                String schematicPath = args[1];
                player.getSessionWrapper().sendMessage(ChatColor.GREEN + "Loading schematic " + schematicPath);
                ForkJoinPool.commonPool().execute(() -> {
                    SchematicLoader.placeSchematic(location.getWorld(), this.getClass().getClassLoader().getResourceAsStream("./"+schematicPath), new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                });
            }
            return;
        }else if(event.getMessage().startsWith("tp")){
            String[] args = event.getMessage().split(" ");
            if (args.length == 4){
                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);
                int z = Integer.parseInt(args[3]);
                player.getSessionWrapper().teleport(new Location(location.getWorld(), x, y, z));
            }
            return;
        }

        enderChest.getPlayers().forEach(each -> each.getSessionWrapper().sendMessage(player.getDisplayName()+" : "+ChatColor.translateAlternateColorCodes('&',event.getMessage())));
        System.out.println(player.getDisplayName()+" : "+event.getMessage());
    }
}
