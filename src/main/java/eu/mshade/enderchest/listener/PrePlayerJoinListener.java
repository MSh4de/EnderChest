package eu.mshade.enderchest.listener;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderchest.axolotl.AxololtConnection;
import eu.mshade.enderchest.entity.DefaultPlayer;
import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.GameMode;
import eu.mshade.enderframe.PlayerInfoBuilder;
import eu.mshade.enderframe.PlayerInfoType;
import eu.mshade.enderframe.entity.metadata.EntityMetadataKey;
import eu.mshade.enderframe.entity.metadata.SkinPartEntityMetadata;
import eu.mshade.enderframe.event.PlayerJoinEvent;
import eu.mshade.enderframe.event.PrePlayerJoinEvent;
import eu.mshade.enderframe.inventory.Inventory;
import eu.mshade.enderframe.inventory.InventoryTracker;
import eu.mshade.enderframe.mojang.GameProfile;
import eu.mshade.enderframe.mojang.SkinPart;
import eu.mshade.enderframe.protocol.MinecraftProtocolPipeline;
import eu.mshade.enderframe.protocol.MinecraftSession;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.World;
import eu.mshade.enderframe.world.WorldRepository;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.enderman.packet.play.MinecraftPacketOutChangeGameState;
import eu.mshade.mwork.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class PrePlayerJoinListener implements EventListener<PrePlayerJoinEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrePlayerJoinListener.class);
//    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private EnderChest enderChest;
    private Chunk chunk = null;

    public PrePlayerJoinListener(EnderChest enderChest) {
        this.enderChest = enderChest;
    }

    @Override
    public void onEvent(PrePlayerJoinEvent event) {
        MinecraftProtocolPipeline minecraftProtocolPipeline = MinecraftProtocolPipeline.get();
        MinecraftSession minecraftSession = event.getMinecraftSession();
        GameProfile gameProfile = minecraftSession.getGameProfile();

        minecraftSession.sendCompression(256);
        minecraftSession.sendLoginSuccess();


        World world = WorldRepository.INSTANCE.getWorld("world");
        Location location = new Location(world, 7.5, 0, 7.5);
        int highest = 0;
        try {
            chunk = location.getChunk().get();
            highest = chunk.getHighest(location.getBlockX(), location.getBlockZ());
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.info("", e);
        }
        location.setY(highest + 1);

        DefaultPlayer player = new DefaultPlayer(location, gameProfile.getId().hashCode(), minecraftSession);
        player.setInetSocketAddress(minecraftSession.getRemoteAddress());
        player.setGameMode(GameMode.CREATIVE);
        minecraftProtocolPipeline.setPlayer(minecraftSession.getChannel(), player);


        minecraftSession.sendJoinGame(world, false);
        minecraftSession.teleport(location);


        minecraftSession.sendPluginMessage("MC|Brand", protocolBuffer -> protocolBuffer.writeString("Enderchest"));
        //default value of flying speed as 0.05
        minecraftSession.sendAbilities(false, false, true, false, 0.5F, 0.1F);
        minecraftSession.sendPacket(new MinecraftPacketOutChangeGameState(3, player.getGameMode().getId()));

        enderChest.addPlayer(player);

        AxololtConnection.INSTANCE.send(axolotlSession -> {
            axolotlSession.sendPlayerJoin(player);
        });


        player.getMetadataKeyValueBucket().setMetadataKeyValue(new SkinPartEntityMetadata(new SkinPart(true, true, true, true, true, true, true)));
        minecraftSession.sendMetadata(player, EntityMetadataKey.INSTANCE.getSKIN_PART());

        PlayerInfoBuilder playerInfoBuilder = PlayerInfoBuilder.of(PlayerInfoType.ADD_PLAYER);
        enderChest.getPlayers().forEach(playerInfoBuilder::withPlayer);
        enderChest.getPlayers().forEach(target -> target.getMinecraftSession().sendPlayerInfo(playerInfoBuilder));

        world.putEntity(player);


        player.joinTickBus(enderChest.getTickBus());

        Inventory playerInventory = player.getInventory();
        InventoryTracker.INSTANCE.add(playerInventory);


        LOGGER.info(String.format("%s join server", player.getGameProfile().getName()));

        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(player);
        EnderFrame.get().getEnderFrameEventBus().publish(playerJoinEvent);

    }
}
