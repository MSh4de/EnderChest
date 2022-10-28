package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderchest.world.SchematicLoader;
import eu.mshade.enderframe.entity.ArmorStand;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.entity.Zombie;
import eu.mshade.enderframe.item.MaterialKey;
import eu.mshade.enderframe.mojang.chat.ChatColor;
import eu.mshade.enderframe.mojang.chat.TextComponent;
import eu.mshade.enderframe.packetevent.PacketChatMessageEvent;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.enderframe.protocol.SessionWrapper;
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

        if (event.getMessage().startsWith("schematic")) {
            String[] args = event.getMessage().split(" ");
            if (args.length == 2) {
                String schematicPath = args[1];
                player.getSessionWrapper().sendMessage(ChatColor.GREEN + "Loading schematic " + schematicPath);
                ForkJoinPool.commonPool().execute(() -> {
                    SchematicLoader.placeSchematic(location.getWorld(), this.getClass().getClassLoader().getResourceAsStream("./" + schematicPath), new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                });
            }
            return;
        } else if (event.getMessage().startsWith("tp")) {
            String[] args = event.getMessage().split(" ");
            if (args.length == 4) {
                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);
                int z = Integer.parseInt(args[3]);
                player.getSessionWrapper().teleport(new Location(location.getWorld(), x, y, z));
            } else if (args.length == 2) {
                String name = args[1];
                enderChest.getPlayers().stream().filter(p -> p.getName().equals(name))
                        .findFirst()
                        .ifPresentOrElse((p -> {
                            player.getSessionWrapper().teleport(p.getLocation());
                        }), () -> {
                            player.getSessionWrapper().sendMessage(ChatColor.RED + "Player not found");
                });
            }
            return;
        } else if (event.getMessage().startsWith("dblock")) {
            String[] args = event.getMessage().split(" ");
            int id = Integer.parseInt(args[1]);
            SessionWrapper sessionWrapper = player.getSessionWrapper();

            if (args.length == 2) {
                for (int i = 0; i < 16; i++) {
                    MaterialKey materialKey = MaterialKey.from(id, i);
                    Vector vector = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ()).add(i * 2, 0, 0);
                    sessionWrapper.sendUnsafeBlockChange(vector, materialKey);
                }
                for (int i = 0; i < 16; i++) {
                    Vector vector = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ()).add(i * 2, 1, 0);
                    sessionWrapper.sendUnsafeBlockChange(vector, MaterialKey.from(63));
                    sessionWrapper.sendSign(vector, TextComponent.of("id: " + id), TextComponent.of("data: " + i));
                }
            } else if (args.length == 3) {
                int data = Integer.parseInt(args[2]);
                MaterialKey materialKey = MaterialKey.from(id, data);
                sessionWrapper.sendUnsafeBlockChange(location.toVector(), materialKey);
            }


            return;

        } else if (event.getMessage().startsWith("bdblock")) {
            String[] args = event.getMessage().split(" ");
            int id = Integer.parseInt(args[1]);
            SessionWrapper sessionWrapper = player.getSessionWrapper();
            int data = Integer.parseInt(args[2]);
            MaterialKey materialKey = MaterialKey.from(id, data);
            sessionWrapper.sendUnsafeBlockChange(location.toVector().add(0, -1, 0), materialKey);
            return;
        }

        enderChest.getPlayers().forEach(each -> each.getSessionWrapper().sendMessage(player.getDisplayName() + " : " + ChatColor.translateAlternateColorCodes('&', event.getMessage())));
        System.out.println(player.getDisplayName() + " : " + event.getMessage());
    }
}
