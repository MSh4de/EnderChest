package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.entity.EntityType;
import eu.mshade.enderframe.entity.Zombie;
import eu.mshade.enderframe.event.entity.PacketChatMessageEvent;
import eu.mshade.enderframe.metadata.MetadataMeaning;
import eu.mshade.enderframe.mojang.chat.ChatColor;
import eu.mshade.enderframe.world.ChunkBuffer;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.WorldBuffer;
import eu.mshade.mwork.event.EventContainer;
import eu.mshade.mwork.event.EventListener;

public class PacketChatMessageHandler implements EventListener<PacketChatMessageEvent> {

    private final DedicatedEnderChest dedicatedEnderChest;

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

            Location location = enderFrameSessionHandler.getEnderFrameSession().getLocation();
            WorldBuffer world = location.getWorld();

            world.spawnEntity(EntityType.ZOMBIE, location);
            System.out.println(location.getChunkBuffer().getEntities().size());
        }else if(args[0].equalsIgnoreCase("!change")){
            if(args[1].equalsIgnoreCase("byte")) {
                Location location = enderFrameSessionHandler.getEnderFrameSession().getLocation();
                ChunkBuffer chunkBuffer = location.getChunkBuffer();
                System.out.println(location.getChunkBuffer().getEntities().size());

                chunkBuffer.getEntities().stream()
                        .findFirst()
                        .map(entity -> ((Zombie) entity))
                        .ifPresent(e -> {
                            System.out.println("Entity id: " + e.getEntityId());
                            e.setChild(true);
                            e.setVillager(true);
                            enderFrameSessionHandler.getEnderFrameSession().sendMetadata(e, MetadataMeaning.IS_CHILD,
                                    MetadataMeaning.IS_VILLAGER);
                        });
            }
        }else if(args[0].equalsIgnoreCase("!link")){
            Location location = enderFrameSessionHandler.getEnderFrameSession().getLocation();
            ChunkBuffer chunkBuffer = location.getChunkBuffer();
            System.out.println(location.getChunkBuffer().getEntities().size());

            chunkBuffer.getEntities().stream().filter(entity -> entity.getEntityId() == Integer.parseInt(args[1])).forEach(each ->{
                PacketMoveHandler.linkedMob.put(enderFrameSessionHandler.getEnderFrameSession(),each.getEntityId());
            });
        }

        dedicatedEnderChest.getEnderFrameSessions().forEach(each -> each.sendMessage(displayName+" : "+ChatColor.translateAlternateColorCodes('&',event.getMessage())));
        System.out.println(displayName+" : "+event.getMessage());
    }
}
