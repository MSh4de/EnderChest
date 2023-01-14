package eu.mshade.enderchest.listener;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderchest.axolotl.AxololtConnection;
import eu.mshade.enderchest.entity.DefaultPlayer;
import eu.mshade.enderframe.GameMode;
import eu.mshade.enderframe.PlayerInfoBuilder;
import eu.mshade.enderframe.PlayerInfoType;
import eu.mshade.enderframe.entity.metadata.SkinPartEntityMetadata;
import eu.mshade.enderframe.event.PrePlayerJoinEvent;
import eu.mshade.enderframe.inventory.*;
import eu.mshade.enderframe.item.*;
import eu.mshade.enderframe.item.metadata.*;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.enderframe.metadata.entity.EntityMetadataKey;
import eu.mshade.enderframe.mojang.GameProfile;
import eu.mshade.enderframe.mojang.SkinPart;
import eu.mshade.enderframe.mojang.chat.ChatColor;
import eu.mshade.enderframe.mojang.chat.TextComponent;
import eu.mshade.enderframe.particle.Particle;
import eu.mshade.enderframe.particle.ParticleBlockCrack;
import eu.mshade.enderframe.protocol.MinecraftProtocolPipeline;
import eu.mshade.enderframe.protocol.MinecraftSession;
import eu.mshade.enderframe.scoreboard.Scoreboard;
import eu.mshade.enderframe.scoreboard.ScoreboardPosition;
import eu.mshade.enderframe.scoreboard.ScoreboardType;
import eu.mshade.enderframe.scoreboard.team.Team;
import eu.mshade.enderframe.scoreboard.team.TeamFriendlyFire;
import eu.mshade.enderframe.scoreboard.team.TeamMode;
import eu.mshade.enderframe.scoreboard.team.TeamNameTagVisibility;
import eu.mshade.enderframe.sound.Sound;
import eu.mshade.enderframe.sound.SoundEffect;
import eu.mshade.enderframe.sound.SoundPosition;
import eu.mshade.enderframe.title.Title;
import eu.mshade.enderframe.title.TitleTime;
import eu.mshade.enderframe.world.WorldRepository;
import eu.mshade.enderframe.world.border.WorldBorder;
import eu.mshade.enderframe.world.border.WorldBorderCenter;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;
import eu.mshade.enderframe.world.World;
import eu.mshade.enderman.packet.play.MinecraftPacketOutChangeGameState;
import eu.mshade.mwork.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

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
        minecraftSession.sendMetadata(player, EntityMetadataKey.SKIN_PART);


        PlayerInfoBuilder playerInfoBuilder = PlayerInfoBuilder.of(PlayerInfoType.ADD_PLAYER);
        enderChest.getPlayers().forEach(playerInfoBuilder::withPlayer);
        enderChest.getPlayers().forEach(target -> target.getMinecraftSession().sendPlayerInfo(playerInfoBuilder));

        player.joinTickBus(enderChest.getTickBus());


        ItemStack grassItemStack = new ItemStack(Material.GRASS);

        MetadataKeyValueBucket metadataKeyValueBucket = grassItemStack.getMetadataKeyValueBucket();
        metadataKeyValueBucket.setMetadataKeyValue(new NameItemStackMetadata("Petit test de mort"));
//        metadataKeyValueBucket.setMetadataKeyValue(new LoreItemStackMetadata(List.of("Ca fonctionne ?", "ALORS", "Es que ça fonctionne ?", "Du coup ?", "Oui ça fonctionne")));
        //metadataKeyValueBucket.setMetadataKeyValue(new UnbreakableItemStackMetadata(true));
        metadataKeyValueBucket.setMetadataKeyValue(new CanPlaceOnItemStackMetadata(List.of(Material.GRANITE)));
        metadataKeyValueBucket.setMetadataKeyValue(new CanDestroyItemStackMetadata(List.of(Material.GRASS)));
        metadataKeyValueBucket.setMetadataKeyValue(new HideFlagsItemStackMetadata(Set.of(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS)));


        Inventory playerInventory = player.getInventory();
        InventoryTracker.INSTANCE.add(playerInventory);

/*        playerInventory.setItemStack(40, new ItemStack(Material.OAK_LOG));
        playerInventory.setItemStack(41, new ItemStack(Material.OAK_LOG));
        playerInventory.setItemStack(42, new ItemStack(Material.OAK_LOG));
        playerInventory.setItemStack(43, new ItemStack(Material.OAK_LOG));*/
//        playerInventory.setItemStack(44, new ItemStack(Material.OAK_WOOD_PLANKS));
/*        playerInventory.setItemStack(1, new ItemStack(Material.OAK_WOOD_PLANKS, 64));
        playerInventory.setItemStack(2, new ItemStack(Material.OAK_WOOD_PLANKS, 32));
        playerInventory.setItemStack(3, new ItemStack(Material.OAK_WOOD_PLANKS, 64));
        playerInventory.setItemStack(4, new ItemStack(Material.OAK_WOOD_PLANKS, 64));*/

//        minecraftSession.sendItemStacks(player.getInventory());


        LOGGER.info(String.format("%s join server", player.getGameProfile().getName()));

        // write TextComponent with rainbow color using CharColor
        TextComponent textComponent = TextComponent.of("coucou")
                .withColor(ChatColor.RED);
        minecraftSession.sendMessage(textComponent);

        // send TextComponent to player
        minecraftSession.sendMessage("Welcome to project MShade");
        minecraftSession.sendHeaderAndFooter("Hey this is test", "and this is test");


        NamedInventory inventory = new NamedInventory("test", InventoryType.CHEST);
//        InventoryTracker.INSTANCE.add(inventory);

        inventory.setItemStack(1, grassItemStack);

//        player.openInventory(inventory);

/*        executorService.scheduleAtFixedRate(() -> {
            grassItemStack.getMetadataKeyValueBucket().setMetadataKeyValue(new NameItemStackMetadata(ChatColor.values()[ThreadLocalRandom.current().nextInt(15)]+"Hello World"));
        },0, 1, TimeUnit.SECONDS);*/

        Scoreboard<String> scoreboard = new Scoreboard<String>(TextComponent.of(ChatColor.BLUE + "Scoreboard"))
                .setScoreboardPosition(ScoreboardPosition.SIDEBAR)
                .setScoreboardType(ScoreboardType.INTEGER)
                .addObjective("TokyFR", 59)
                .addObjective("RealAlpha", 70);
        //scoreboard.showScoreboard(player);


        SoundEffect soundEffect = new SoundEffect(Sound.RANDOM_CHESTOPEN, new SoundPosition(7, highest + 2, 7), 100F, 63);
        soundEffect.createSound(player);

        Title title = new Title()
                .setTitle(TextComponent.of("Test du titre"))
                .setSubtitle(TextComponent.of("Test du sous titre"))
                .setTitleTime(new TitleTime(10, 100, 10));

//        title.showTitle(player);

        WorldBorder worldBorder = new WorldBorder()
                .setWorldBorderCenter(new WorldBorderCenter(7d, 7d))
                .setRadius(20d)
                .setWarningBlocks(20 - 1)
                .setWarningTime(2)
                .setPortalTeleportLimit(29999984);

        //worldBorder.createWorldBorder(player);

        /*
        executorService.schedule(() -> {
            Section section = chunk.getSection(60);
            Arrays.fill(section.getBlocks(), 2);
            sessionWrapper.sendSection(section);

        }, 5, TimeUnit.SECONDS);

         */

        Team team = new Team.TeamBuilder()
                .setTeamName(scoreboard.getScoreboardId())
                .setTeamMode(TeamMode.CREATE_TEAM)
                .setTeamDisplayName("MCHAD")
                .setTeamPrefix("Eh")
                .setTeamSuffix("HE")
                .setTeamColor(ChatColor.YELLOW)
                .setPlayersName(List.of("TokyFR", "_RealAlpha_"))
                .setTeamNameTagVisibility(TeamNameTagVisibility.ALWAYS)
                .setTeamFriendlyFire(TeamFriendlyFire.ON)
                .buildTeam();

        //sessionWrapper.sendTeams(team);

        Particle particle = new ParticleBlockCrack(false, new Vector(7, highest + 2, 7), new Vector(0, 0, 0), 1F, 100, Material.GRASS, 0);
        //Particle particle = new Particle(ParticleType.FIREWORK, false, new Vector(7, highest + 2, 7), new Vector(0, 0, 0), 1F, 100);
/*        executorService.scheduleAtFixedRate(() -> {
//            sessionWrapper.sendTeams(team);
        }, 0, 1000, TimeUnit.MILLISECONDS);*/

    }
}
