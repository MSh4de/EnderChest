package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderchest.entity.DefaultPlayer;
import eu.mshade.enderframe.GameMode;
import eu.mshade.enderframe.PlayerInfoBuilder;
import eu.mshade.enderframe.PlayerInfoType;
import eu.mshade.enderframe.entity.metadata.SkinPartEntityMetadata;
import eu.mshade.enderframe.inventory.EquipmentSlot;
import eu.mshade.enderframe.inventory.Inventory;
import eu.mshade.enderframe.inventory.InventoryType;
import eu.mshade.enderframe.inventory.PlayerInventory;
import eu.mshade.enderframe.item.*;
import eu.mshade.enderframe.item.metadata.*;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.enderframe.metadata.attribute.Attribute;
import eu.mshade.enderframe.metadata.attribute.AttributeModifier;
import eu.mshade.enderframe.metadata.attribute.AttributeOperation;
import eu.mshade.enderframe.metadata.entity.EntityMetadataKey;
import eu.mshade.enderframe.mojang.Color;
import eu.mshade.enderframe.mojang.GameProfile;
import eu.mshade.enderframe.mojang.Property;
import eu.mshade.enderframe.mojang.SkinPart;
import eu.mshade.enderframe.packetevent.PacketFinallyJoinEvent;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.enderframe.protocol.SessionWrapper;
import eu.mshade.enderframe.world.Chunk;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.World;
import eu.mshade.enderman.packet.play.PacketOutChangeGameState;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PacketFinallyJoinHandler implements EventListener<PacketFinallyJoinEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketFinallyJoinHandler.class);
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private EnderChest enderChest;

    public PacketFinallyJoinHandler(EnderChest enderChest) {
        this.enderChest = enderChest;
    }

    @Override
    public void onEvent(PacketFinallyJoinEvent event, ParameterContainer parameterContainer) {
        ProtocolPipeline protocolPipeline = ProtocolPipeline.get();
        Channel channel = parameterContainer.getContainer(Channel.class);
        SessionWrapper sessionWrapper = protocolPipeline.getSessionWrapper(channel);
        GameProfile gameProfile = sessionWrapper.getGameProfile();

        sessionWrapper.sendCompression(256);
        sessionWrapper.sendLoginSuccess();


        World world = enderChest.getWorldManager().getWorld("world");
        Location location = new Location(world, 7, 0, 7);
        int highest = 0;
        try {
            Chunk chunk = location.getChunk().get();
            highest = chunk.getHighest(location.getBlockX(), location.getBlockZ());
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.info("");
        }
        location.setY(highest + 1);

        DefaultPlayer player = new DefaultPlayer(location, gameProfile.getId().hashCode(), sessionWrapper);
        player.setGameMode(GameMode.CREATIVE);
        protocolPipeline.setPlayer(channel, player);


        sessionWrapper.sendJoinGame(world, false);
        sessionWrapper.teleport(location);


        sessionWrapper.sendPluginMessage("MC|Brand", protocolBuffer -> protocolBuffer.writeString("Enderchest"));
        //default value of flying speed as 0.05
        sessionWrapper.sendAbilities(false, false, true, false, 2F, 0.1F);
        sessionWrapper.sendPacket(new PacketOutChangeGameState(3, player.getGameMode().getId()));

        enderChest.addPlayer(player);


        player.getMetadataKeyValueBucket().setMetadataKeyValue(new SkinPartEntityMetadata(new SkinPart(true, true, true, true, true, true, true)));
        sessionWrapper.sendMetadata(player, EntityMetadataKey.SKIN_PART);


        PlayerInfoBuilder playerInfoBuilder = PlayerInfoBuilder.of(PlayerInfoType.ADD_PLAYER);
        enderChest.getPlayers().forEach(playerInfoBuilder::withPlayer);
        enderChest.getPlayers().forEach(target -> target.getSessionWrapper().sendPlayerInfo(playerInfoBuilder));

        player.joinTickBus(enderChest.getTickBus());

        ItemStack itemStack = new ItemStack(Material.WOODEN_PICKAXE, 1);
        /*
        MetadataKeyValueBucket metadataKeyValueBucket = itemStack.getMetadataKeyValueBucket();
        metadataKeyValueBucket.setMetadataKeyValue(new NameItemStackMetadata("Petit test de mort"));
        metadataKeyValueBucket.setMetadataKeyValue(new LoreItemStackMetadata(List.of("Ca fonctionne ?", "ALORS", "Es que ça fonctionne ?", "Du coup ?", "Oui ça fonctionne pd")));
        //metadataKeyValueBucket.setMetadataKeyValue(new UnbreakableItemStackMetadata(true));
        metadataKeyValueBucket.setMetadataKeyValue(new CanPlaceOnItemStackMetadata(List.of(Material.GRANITE)));
        metadataKeyValueBucket.setMetadataKeyValue(new CanDestroyItemStackMetadata(List.of(Material.GRASS)));
        metadataKeyValueBucket.setMetadataKeyValue(new HideFlagsItemStackMetadata(Set.of(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS)));
        metadataKeyValueBucket.setMetadataKeyValue(new ColorItemStackMetadata(Color.BLUE));
        metadataKeyValueBucket.setMetadataKeyValue(new SkullOwnerItemStackMetadata(new GameProfile(UUID.randomUUID(), "okok", List.of(new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTkxYTc3OThkZDBhNDZiNmIzZjE3YmIwMzBhNmFkZjZlNDkwNjRjNGI5M2QxZDlkNTYzNDc4OWM4OTQ5In19fQ==")))));


        metadataKeyValueBucket.setMetadataKeyValue(new AttributeModifiersItemStackMetadata(List.of(new ItemStackAttributeModifier(Attribute.MAX_HEALTH, "test", EquipmentSlot.MAIN_HAND, new AttributeModifier(UUID.randomUUID(), 10, AttributeOperation.ADD_NUMBER)))));


         */
        Inventory playerInventory = player.getPlayerInventory();

        playerInventory.setItemStack(36, itemStack);
        playerInventory.setItemStack(37, itemStack);
        playerInventory.setItemStack(38, itemStack);
        playerInventory.setItemStack(39, itemStack);
        playerInventory.setItemStack(40, new ItemStack(Material.OAK_WOOD));
        playerInventory.setItemStack(41, new ItemStack(Material.OAK_WOOD));
        playerInventory.setItemStack(42, new ItemStack(Material.OAK_WOOD));
        playerInventory.setItemStack(43, new ItemStack(Material.OAK_WOOD));
        playerInventory.setItemStack(44, new ItemStack(Material.OAK_WOOD_PLANKS));
        playerInventory.setItemStack(0, itemStack);
        playerInventory.setItemStack(1, new ItemStack(Material.OAK_WOOD_PLANKS, 64));
        playerInventory.setItemStack(2, new ItemStack(Material.OAK_WOOD_PLANKS, 32));
        playerInventory.setItemStack(3, new ItemStack(Material.OAK_WOOD_PLANKS, 64));
        playerInventory.setItemStack(4, new ItemStack(Material.OAK_WOOD_PLANKS, 64));

        sessionWrapper.sendItemStacks(player.getPlayerInventory());

        //sessionWrapper.sendItemStack();


        //sessionWrapper.sendPacket(new PacketOutSetItemStack(36, 0, itemStack));


        //enderFrameSession.getPlayer().getEnderFrameSessionHandler().sendPacket(new PacketOutSpawnPosition(new BlockPosition(location.getBlockX(),location.getBlockY(),location.getBlockZ())));

        //enderFrameSession.sendPosition(location);
        //sessionWrapper.sendSquareChunk(10, location.getChunkX(), location.getChunkZ(), world);


        LOGGER.info(String.format("%s join server", player.getGameProfile().getName()));

        sessionWrapper.sendMessage("Welcome to project MShade");
        sessionWrapper.sendHeadAndFooter("Hey this is test", "and this is test");


        Inventory inventory = new Inventory("test", InventoryType.CHEST);
        int slot = 0;

        for (MaterialKey materialKey : Material.getRegisteredMaterials()) {
            inventory.setItemStack(slot++, new ItemStack(materialKey));
        }




        sessionWrapper.sendOpenInventory(inventory);
        sessionWrapper.sendItemStacks(inventory);

        /*
        executorService.schedule(() -> {
            sessionWrapper.sendOpenInventory(new Inventory("test4", InventoryType.HOPPER));
        }, 10, TimeUnit.SECONDS);

         */









    }


}
