package eu.mshade.enderchest.listener.packet;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderchest.world.SchematicLoader;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.item.MaterialKey;
import eu.mshade.enderframe.mojang.chat.ChatColor;
import eu.mshade.enderframe.mojang.chat.TextComponent;
import eu.mshade.enderframe.packetevent.MinecraftPacketChatMessageEvent;
import eu.mshade.enderframe.protocol.MinecraftSession;
import eu.mshade.enderframe.virtualserver.VirtualWorld;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;
import eu.mshade.mwork.event.EventListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public class MinecraftPacketChatMessageListener implements EventListener<MinecraftPacketChatMessageEvent> {


    @Override
    public void onEvent(MinecraftPacketChatMessageEvent event) {
        Player player = event.getPlayer();
        MinecraftSession minecraftSession = player.getMinecraftSession();
        Location location = player.getLocation();


        if (event.getMessage().startsWith("schematic")) {
            String[] args = event.getMessage().split(" ");
            if (args.length == 2) {
                String schematicPath = args[1];
                minecraftSession.sendMessage(ChatColor.GREEN + "Loading schematic " + schematicPath);
                ForkJoinPool.commonPool().execute(() -> {
                    SchematicLoader.placeSchematic(location.getWorld(),  schematicPath, new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                });
            }
            return;
        } else if (event.getMessage().startsWith("tp")) {
            String[] args = event.getMessage().split(" ");
            if (args.length == 4) {
                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);
                int z = Integer.parseInt(args[3]);
                minecraftSession.teleport(new Location(location.getWorld(), x, y, z));
            } else if (args.length == 2) {
                String name = args[1];
                Player target = EnderChest.INSTANCE.getMinecraftServer().getPlayer(name);
                if (target != null) {
                    minecraftSession.teleport(target.getLocation());
                }
            }
            return;
        } else if (event.getMessage().startsWith("dblock")) {
            String[] args = event.getMessage().split(" ");
            int id = Integer.parseInt(args[1]);

            if (args.length == 2) {
                for (int i = 0; i < 16; i++) {
                    MaterialKey materialKey = MaterialKey.from(id, i);
                    Vector vector = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ()).add(i * 2, 0, 0);
                    minecraftSession.sendUnsafeBlockChange(vector, materialKey);
                }
                for (int i = 0; i < 16; i++) {
                    Vector vector = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ()).add(i * 2, 1, 0);
                    minecraftSession.sendUnsafeBlockChange(vector, MaterialKey.from(63));
                    minecraftSession.sendSign(vector, TextComponent.of("id: " + id), TextComponent.of("data: " + i));
                }
            } else if (args.length == 3) {
                int data = Integer.parseInt(args[2]);
                MaterialKey materialKey = MaterialKey.from(id, data);
                minecraftSession.sendUnsafeBlockChange(location.toVector(), materialKey);
            }


            return;

        } else if (event.getMessage().startsWith("bdblock")) {
            String[] args = event.getMessage().split(" ");
            int id = Integer.parseInt(args[1]);
            int data = Integer.parseInt(args[2]);
            MaterialKey materialKey = MaterialKey.from(id, data);
            minecraftSession.sendUnsafeBlockChange(location.toVector().add(0, -1, 0), materialKey);
            return;
        } else if (event.getMessage().startsWith("virtualWorld")){
            String[] args = event.getMessage().split(" ");
            if (args.length <= 1) return;
            String command = args[1];
            if (command.equals("create")){
                if (args.length == 3){
                    String virtualWorldName = args[2];
                    EnderChest.INSTANCE.getVirtualWorldManager().createVirtualWorld(virtualWorldName, location.getWorld());
                    minecraftSession.sendMessage(ChatColor.GREEN + "Virtual world " + virtualWorldName + " created");
                }
            }else if (command.equals("list")) {
                EnderChest.INSTANCE.getVirtualWorldManager().getVirtualWorlds().forEach(virtualWorld -> {
                    minecraftSession.sendMessage(ChatColor.GREEN + virtualWorld.getName());
                });
            }else if (command.equals("join")){
                if (args.length == 3){
                    String virtualWorldName = args[2];
                    VirtualWorld virtualWorld = EnderChest.INSTANCE.getVirtualWorldManager().getVirtualWorld(virtualWorldName);
                    if (virtualWorld == null){
                        minecraftSession.sendMessage(ChatColor.RED + "Virtual world not found");
                        return;
                    }
                    CompletableFuture.runAsync(() -> {
                            player.joinWorld(virtualWorld);
                        minecraftSession.sendMessage(ChatColor.GREEN + "Joined virtual world " + virtualWorldName);
                    });

                }
            }else if (command.equals("leave")){
                CompletableFuture.runAsync(() -> {
                    minecraftSession.sendMessage(ChatColor.GREEN + "Left virtual world "+ player.getLocation().getWorld().getName());
                    player.joinWorld(EnderChest.INSTANCE.getWorldRepository().getWorld("world"));
                });
            }
            return;
        }

        EnderChest.INSTANCE.getMinecraftServer().getOnlinePlayers().forEach(each -> each.getMinecraftSession().sendMessage(player.getDisplayName() + " : " + ChatColor.translateAlternateColorCodes('&', event.getMessage())));
        System.out.println(player.getDisplayName() + " : " + event.getMessage());
    }
}
