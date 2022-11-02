package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderchest.entity.DefaultBat;
import eu.mshade.enderchest.entity.DefaultPig;
import eu.mshade.enderchest.entity.DefaultPlayer;
import eu.mshade.enderframe.GameMode;
import eu.mshade.enderframe.PlayerInfoBuilder;
import eu.mshade.enderframe.PlayerInfoType;
import eu.mshade.enderframe.entity.EntityIdManager;
import eu.mshade.enderframe.entity.metadata.EntityMetadataKey;
import eu.mshade.enderframe.entity.metadata.SkinPartEntityMetadata;
import eu.mshade.enderframe.inventory.EquipmentSlot;
import eu.mshade.enderframe.inventory.Inventory;
import eu.mshade.enderframe.inventory.InventoryType;
import eu.mshade.enderframe.item.*;
import eu.mshade.enderframe.item.metadata.*;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.enderframe.metadata.attribute.Attribute;
import eu.mshade.enderframe.metadata.attribute.AttributeModifier;
import eu.mshade.enderframe.metadata.attribute.AttributeOperation;
import eu.mshade.enderframe.mojang.Color;
import eu.mshade.enderframe.mojang.GameProfile;
import eu.mshade.enderframe.mojang.Property;
import eu.mshade.enderframe.mojang.SkinPart;
import eu.mshade.enderframe.mojang.chat.ChatColor;
import eu.mshade.enderframe.mojang.chat.TextComponent;
import eu.mshade.enderframe.packetevent.PacketFinallyJoinEvent;
import eu.mshade.enderframe.particle.Particle;
import eu.mshade.enderframe.particle.ParticleBlockCrack;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.enderframe.protocol.SessionWrapper;
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
import eu.mshade.enderframe.world.border.WorldBorder;
import eu.mshade.enderframe.world.border.WorldBorderCenter;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;
import eu.mshade.enderframe.world.World;
import eu.mshade.enderman.packet.play.PacketOutChangeGameState;
import eu.mshade.mwork.event.EventListener;
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
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private EnderChest enderChest;
    private Chunk chunk = null;

    public PacketFinallyJoinHandler(EnderChest enderChest) {
        this.enderChest = enderChest;
    }

    @Override
    public void onEvent(PacketFinallyJoinEvent event) {
        ProtocolPipeline protocolPipeline = ProtocolPipeline.get();
        SessionWrapper sessionWrapper = event.getSessionWrapper();
        GameProfile gameProfile = sessionWrapper.getGameProfile();

        sessionWrapper.sendCompression(256);
        sessionWrapper.sendLoginSuccess();


        World world = enderChest.getWorldManager().getWorld("world");
        Location location = new Location(world, 7.5, 0, 7.5);
        int highest = 0;
        try {
            chunk = location.getChunk().get();
            highest = chunk.getHighest(location.getBlockX(), location.getBlockZ());
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.info("");
        }
        location.setY(highest+1);

        DefaultPlayer player = new DefaultPlayer(location, gameProfile.getId().hashCode(), sessionWrapper);
        player.setGameMode(GameMode.CREATIVE);
        protocolPipeline.setPlayer(sessionWrapper.getChannel(), player);


        sessionWrapper.sendJoinGame(world, false);
        sessionWrapper.teleport(location);


        sessionWrapper.sendPluginMessage("MC|Brand", protocolBuffer -> protocolBuffer.writeString("Enderchest"));
        //default value of flying speed as 0.05
        sessionWrapper.sendAbilities(false, false, true, false, 0.5F, 0.1F);
        sessionWrapper.sendPacket(new PacketOutChangeGameState(3, player.getGameMode().getId()));

        enderChest.addPlayer(player);


        player.getMetadataKeyValueBucket().setMetadataKeyValue(new SkinPartEntityMetadata(new SkinPart(true, true, true, true, true, true, true)));
        sessionWrapper.sendMetadata(player, EntityMetadataKey.INSTANCE.getSKIN_PART());


        PlayerInfoBuilder playerInfoBuilder = PlayerInfoBuilder.of(PlayerInfoType.ADD_PLAYER);
        enderChest.getPlayers().forEach(playerInfoBuilder::withPlayer);
        enderChest.getPlayers().forEach(target -> target.getSessionWrapper().sendPlayerInfo(playerInfoBuilder));

        int chunkX = location.getChunkX();
        int chunkZ = location.getChunkZ();

        for (int x = chunkX - 5; x <= chunkX + 5; x++) {
            for (int z = chunkZ - 5; z <= chunkZ + 5; z++) {
                Location entityLocation = player.getLocation().clone().add(x * 16, 0, z * 16);
                entityLocation.getChunk().thenAccept(chunk -> {
                    chunk.getEntities().add(new DefaultBat(entityLocation, EntityIdManager.get().getFreeId()));
                });
            }
        }

        player.joinTickBus(enderChest.getTickBus());


        ItemStack grassItemStack = new ItemStack(Material.GRASS);

        MetadataKeyValueBucket metadataKeyValueBucket = grassItemStack.getMetadataKeyValueBucket();
        metadataKeyValueBucket.setMetadataKeyValue(new NameItemStackMetadata("Petit test de mort"));
        metadataKeyValueBucket.setMetadataKeyValue(new LoreItemStackMetadata(List.of("Ca fonctionne ?", "ALORS", "Es que ça fonctionne ?", "Du coup ?", "Oui ça fonctionne pd")));
        //metadataKeyValueBucket.setMetadataKeyValue(new UnbreakableItemStackMetadata(true));
        metadataKeyValueBucket.setMetadataKeyValue(new CanPlaceOnItemStackMetadata(List.of(Material.GRANITE)));
        metadataKeyValueBucket.setMetadataKeyValue(new CanDestroyItemStackMetadata(List.of(Material.GRASS)));
        metadataKeyValueBucket.setMetadataKeyValue(new HideFlagsItemStackMetadata(Set.of(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS)));
        metadataKeyValueBucket.setMetadataKeyValue(new ColorItemStackMetadata(Color.BLUE));
        metadataKeyValueBucket.setMetadataKeyValue(new SkullOwnerItemStackMetadata(new GameProfile(UUID.randomUUID(), "okok", List.of(new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTkxYTc3OThkZDBhNDZiNmIzZjE3YmIwMzBhNmFkZjZlNDkwNjRjNGI5M2QxZDlkNTYzNDc4OWM4OTQ5In19fQ==")))));
        


        metadataKeyValueBucket.setMetadataKeyValue(new AttributeModifiersItemStackMetadata(List.of(new ItemStackAttributeModifier(Attribute.MAX_HEALTH, "test", EquipmentSlot.MAIN_HAND, new AttributeModifier(UUID.randomUUID(), 10, AttributeOperation.ADD_NUMBER)))));


        Inventory playerInventory = player.getInventory();

        /*playerInventory.setItemStack(40, new ItemStack(Material.OAK_LOG));
        playerInventory.setItemStack(41, new ItemStack(Material.OAK_LOG));
        playerInventory.setItemStack(42, new ItemStack(Material.OAK_LOG));
        playerInventory.setItemStack(43, new ItemStack(Material.OAK_LOG));*/
        playerInventory.setItemStack(44, new ItemStack(Material.OAK_WOOD_PLANKS));
        playerInventory.setItemStack(1, new ItemStack(Material.OAK_WOOD_PLANKS, 64));
        playerInventory.setItemStack(2, new ItemStack(Material.OAK_WOOD_PLANKS, 32));
        playerInventory.setItemStack(3, new ItemStack(Material.OAK_WOOD_PLANKS, 64));
        playerInventory.setItemStack(4, new ItemStack(Material.OAK_WOOD_PLANKS, 64));

        sessionWrapper.sendItemStacks(player.getInventory());

        LOGGER.info(String.format("%s join server", player.getGameProfile().getName()));

        // write TextComponent with rainbow color using CharColor
        TextComponent textComponent =  TextComponent.of("§1c§2o§3r§4t§5i§6n§7g§8e§9r§a");
        sessionWrapper.sendMessage(textComponent);

        // send TextComponent to player
        sessionWrapper.sendMessage("Welcome to project MShade");
        sessionWrapper.sendHeaderAndFooter("Hey this is test", "and this is test");


        Inventory inventory = new Inventory("test", InventoryType.CHEST);
        int slot = 0;

        for (MaterialKey materialKey : Material.getRegisteredMaterials()) {
            if (slot == inventory.getInventoryKey().getDefaultSlot()) {
                break;
            }
            inventory.setItemStack(slot++, new ItemStack(materialKey));
        }
        // sessionWrapper.sendOpenInventory(inventory);
        // sessionWrapper.sendItemStacks(inventory);


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

        title.showTitle(player);

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
        executorService.schedule(() ->  sessionWrapper.sendTeams(team), 3, TimeUnit.SECONDS);

    }
}
