package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.EntityType;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.entity.Zombie;
import eu.mshade.enderframe.item.*;
import eu.mshade.enderframe.item.entities.BannerItemStack;
import eu.mshade.enderframe.item.entities.LeatherArmorItemStack;
import eu.mshade.enderframe.metadata.MetadataMeaning;
import eu.mshade.enderframe.mojang.Color;
import eu.mshade.enderframe.mojang.chat.ChatColor;
import eu.mshade.enderframe.packetevent.PacketChatMessageEvent;
import eu.mshade.enderframe.world.ChunkBuffer;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.WorldBuffer;
import eu.mshade.enderman.packet.play.PacketOutSetSlot;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PacketChatMessageHandler implements EventListener<PacketChatMessageEvent> {

    private final DedicatedEnderChest dedicatedEnderChest;

    public PacketChatMessageHandler(DedicatedEnderChest dedicatedEnderChest) {
        this.dedicatedEnderChest = dedicatedEnderChest;
    }

    @Override
    public void onEvent(PacketChatMessageEvent event, ParameterContainer eventContainer) {
        Player player = event.getPlayer();
        EnderFrameSessionHandler enderFrameSessionHandler = player.getEnderFrameSessionHandler();

        String displayName = player.getGameProfile().getName();
        if(event.getMessage().startsWith("/"))return;
        String[] args = event.getMessage().split(" ");

        if (args[0].equalsIgnoreCase("!speed")){
            player.getEnderFrameSession().sendAbilities(false, true, true, false, Float.parseFloat(args[1]), 0.2F);
        }else if(args[0].equalsIgnoreCase("!spawn")){
            System.out.println("SPAWN ENTITY");

            Location location = player.getLocation();
            WorldBuffer world = location.getWorld();

            //enderFrameSessionHandler.sendPacket(new PacketOutSpawnPlayer(player));
            //enderFrameSessionHandler.sendPacket(new PacketOutSpawnPlayer(player));
            Entity entity = world.spawnEntity(EntityType.ZOMBIE, location);
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(entity::tick, 1, 1, TimeUnit.SECONDS);

        }else if(args[0].equalsIgnoreCase("!change")){
            if(args[1].equalsIgnoreCase("byte")) {
                Location location = player.getLocation();
                ChunkBuffer chunkBuffer = location.getChunkBuffer();

                chunkBuffer.getEntities().stream()
                        .findFirst()
                        .map(entity -> ((Zombie) entity))
                        .ifPresent(e -> {
                            System.out.println("Entity id: " + e.getEntityId());
                            e.setAdult();
                            e.setVillager(true);
                            enderFrameSessionHandler.getEnderFrameSession().sendMetadata(e, MetadataMeaning.IS_CHILD,
                                    MetadataMeaning.IS_VILLAGER);
                        });
            }
        }else if(args[0].equalsIgnoreCase("!link")){
            Location location = player.getLocation();
            ChunkBuffer chunkBuffer = location.getChunkBuffer();
            System.out.println(location.getChunkBuffer().getEntities().size());

            chunkBuffer.getEntities().stream().filter(entity -> entity.getEntityId() == Integer.parseInt(args[1])).forEach(each ->{
            });
        }else if(args[0].equalsIgnoreCase("!setColor")){
            Color color = Color.fromRGB(Integer.parseInt(args[1]));
            enderFrameSessionHandler.sendPacket(new PacketOutSetSlot(36, new LeatherArmorItemStack(Material.LEATHER_HELMET,1,0).setColor(color)));
            enderFrameSessionHandler.sendPacket(new PacketOutSetSlot(37, new LeatherArmorItemStack(Material.LEATHER_CHESTPLATE,1,0).setColor(color)));
            enderFrameSessionHandler.sendPacket(new PacketOutSetSlot(38, new LeatherArmorItemStack(Material.LEATHER_LEGGINGS,1,0).setColor(color)));
            enderFrameSessionHandler.sendPacket(new PacketOutSetSlot(39, new LeatherArmorItemStack(Material.LEATHER_BOOTS,1,0).setColor(color)));
        }else if(args[0].equalsIgnoreCase("!setBanner")){
            if(args[1].equalsIgnoreCase("pattern")){
                enderFrameSessionHandler.sendPacket(new PacketOutSetSlot(40,new BannerItemStack(Material.MAGENTA_BANNER,1,0).addPattern(new Pattern(BannerColor.values()[Integer.parseInt(args[2])], BannerPattern.getByIdentifier(args[3])))));
            }else{
                enderFrameSessionHandler.sendPacket(new PacketOutSetSlot(40,new BannerItemStack(Material.MAGENTA_BANNER,1,0).setColor(BannerColor.values()[Integer.parseInt(args[1])])));
            }
        }

        dedicatedEnderChest.getPlayers().forEach(each -> each.sendMessage(displayName+" : "+ChatColor.translateAlternateColorCodes('&',event.getMessage())));
        System.out.println(displayName+" : "+event.getMessage());
    }
}
