package eu.mshade.enderchest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.mshade.enderchest.entity.*;
import eu.mshade.enderchest.entity.marshal.common.*;
import eu.mshade.enderchest.listener.*;
import eu.mshade.enderchest.entity.marshal.entity.*;
import eu.mshade.enderchest.entity.marshal.world.DefaultChunkMarshal;
import eu.mshade.enderchest.entity.marshal.world.DefaultLocationMarshal;
import eu.mshade.enderchest.entity.marshal.world.DefaultSectionMarshal;
import eu.mshade.enderchest.protocol.listener.*;
import eu.mshade.enderchest.redstone.Redstone;
import eu.mshade.enderchest.redstone.protocol.RedstonePacketIn;
import eu.mshade.enderchest.redstone.protocol.RedstonePacketInDeserializer;
import eu.mshade.enderchest.world.DefaultChunkBuffer;
import eu.mshade.enderchest.world.DefaultSectionBuffer;
import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.GameMode;
import eu.mshade.enderframe.entity.*;
import eu.mshade.enderframe.event.*;
import eu.mshade.enderframe.packetevent.*;
import eu.mshade.enderframe.mojang.GameProfile;
import eu.mshade.enderframe.mojang.Property;
import eu.mshade.enderframe.mojang.chat.*;
import eu.mshade.enderframe.world.*;
import eu.mshade.mwork.MWork;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;
import eu.mshade.mwork.event.EventBus;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class EnderChest {

    private final EventLoopGroup eventLoopGroup;
    private final Redstone redstone;
    private final DedicatedEnderChest dedicatedEnderChest;
    private final Logger logger = LoggerFactory.getLogger(EnderChest.class);


    public EnderChest() {
        System.out.println("\n" +
                "███████╗███╗░░██╗██████╗░███████╗██████╗░░█████╗░██╗░░██╗███████╗░██████╗████████╗\n" +
                "██╔════╝████╗░██║██╔══██╗██╔════╝██╔══██╗██╔══██╗██║░░██║██╔════╝██╔════╝╚══██╔══╝\n" +
                "█████╗░░██╔██╗██║██║░░██║█████╗░░██████╔╝██║░░╚═╝███████║█████╗░░╚█████╗░░░░██║░░░\n" +
                "██╔══╝░░██║╚████║██║░░██║██╔══╝░░██╔══██╗██║░░██╗██╔══██║██╔══╝░░░╚═══██╗░░░██║░░░\n" +
                "███████╗██║░╚███║██████╔╝███████╗██║░░██║╚█████╔╝██║░░██║███████╗██████╔╝░░░██║░░░\n" +
                "╚══════╝╚═╝░░╚══╝╚═════╝░╚══════╝╚═╝░░╚═╝░╚════╝░╚═╝░░╚═╝╚══════╝╚═════╝░░░░╚═╝░░░");
        logger.info("Starting EnderChest");
        this.eventLoopGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        this.dedicatedEnderChest  = new DedicatedEnderChest(eventLoopGroup);

        TextComponentSerializer textComponentSerializer = new TextComponentSerializer();
        ObjectMapper objectMapper = MWork.get().getObjectMapper();

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(RedstonePacketIn.class, new RedstonePacketInDeserializer());
        simpleModule.addSerializer(TextComponentEntry.class, textComponentSerializer);
        simpleModule.addSerializer(TextComponent.class, textComponentSerializer);
        simpleModule.addSerializer(TextClickEvent.class, new TextClickEventSerializer());

        objectMapper.registerModule(simpleModule);

        this.redstone = new Redstone(this);

        EnderFrame enderFrame = EnderFrame.get();
        EventBus<PacketEvent> packetEventBus = enderFrame.getPacketEventBus();

        packetEventBus.subscribe(PacketHandshakeEvent.class, new PacketHandshakeListener(dedicatedEnderChest));
        packetEventBus.subscribe(ServerPingEvent.class, new ServerPingListener());
        packetEventBus.subscribe(ServerStatusEvent.class, new ServerStatusListener(redstone));
        packetEventBus.subscribe(PacketLoginEvent.class, new PacketLoginHandler(dedicatedEnderChest));
        packetEventBus.subscribe(PacketEncryptionEvent.class, new PacketEncryptionHandler(dedicatedEnderChest));
        packetEventBus.subscribe(PacketKeepAliveEvent.class, new PacketKeepAliveHandler(dedicatedEnderChest));
        packetEventBus.subscribe(PacketClientSettingsEvent.class, new PacketClientSettingsHandler());
        packetEventBus.subscribe(PacketChatMessageEvent.class, new PacketChatMessageHandler(dedicatedEnderChest));
        packetEventBus.subscribe(PacketFinallyJoinEvent.class, new PacketFinallyJoinHandler(dedicatedEnderChest));
        packetEventBus.subscribe(PacketMoveEvent.class, new PacketMoveHandler(dedicatedEnderChest));
        packetEventBus.subscribe(PacketQuitEvent.class, new PacketQuitHandler(dedicatedEnderChest));
        packetEventBus.subscribe(PacketEntityActionEvent.class, new PacketEntityActionHandler());

        EventBus<EnderFrameEvent> enderFrameEventBus = enderFrame.getEnderFrameEventBus();

        enderFrameEventBus.subscribe(EntityUnseeEvent.class, new EntityUnseeHandler());
        enderFrameEventBus.subscribe(EntitySeeEvent.class, new EntitySeeHandler());

        enderFrameEventBus.subscribe(ChunkSeeEvent.class, new ChunkSeeHandler());
        enderFrameEventBus.subscribe(ChunkUnseeEvent.class, new ChunkUnseeHandler());
        enderFrameEventBus.subscribe(EntityMoveEvent.class, new EntityMoveHandler());
        enderFrameEventBus.subscribe(EntityTeleportEvent.class, new EntityTeleportHandler());
        enderFrameEventBus.subscribe(EntityChunkChangeEvent.class, new EntityChunkChangeHandler());

        enderFrameEventBus.subscribe(ChunkUnloadEvent.class, new ChunkUnloadHandler());
        enderFrameEventBus.subscribe(ChunkLoadEvent.class, new ChunkLoadHandler());
        enderFrameEventBus.subscribe(WatchdogSeeEvent.class, new WatchdogSeeHandler());
        enderFrameEventBus.subscribe(WatchdogUnseeEvent.class, new WatchdogUnseeHandler());

        BinaryTagMarshal binaryTagMarshal = MWork.get().getBinaryTagMarshal();

        binaryTagMarshal.registerAdaptor(Arrays.asList(SectionBuffer.class, DefaultSectionBuffer.class), new DefaultSectionMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(ChunkBuffer.class, DefaultChunkBuffer.class), new DefaultChunkMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Zombie.class, DefaultZombieEntity.class), new DefaultZombieMarshal());
        binaryTagMarshal.registerAdaptor(Location.class, new DefaultLocationMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Player.class, DefaultPlayerEntity.class), new DefaultPlayerMarshal());
        binaryTagMarshal.registerAdaptor(Vector.class, new DefaultVectorMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Blaze.class, DefaultBlazeEntity.class), new DefaultBlazeMarshal());
        binaryTagMarshal.registerAdaptor(Rotation.class, new DefaultRotationMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(ArmorStand.class, DefaultArmorStandEntity.class), new DefaultArmorStandMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Arrow.class, DefaultArrowEntity.class), new DefaultArrowMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Ageable.class, DefaultAgeableEntity.class), new DefaultAgeableMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Rideable.class, DefaultRideableEntity.class), new DefaultRideableMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Pig.class, DefaultPigEntity.class), new DefaultPigMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Bat.class, DefaultBatEntity.class), new DefaultBatMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Spider.class, DefaultSpiderEntity.class), new DefaultSpiderMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(CaveSpider.class, DefaultCaveSpiderEntity.class), new DefaultCaveSpiderMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Tameable.class, DefaultTameableEntity.class), new DefaultTameableMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Chicken.class, DefaultChickenEntity.class), new DefaultChickenMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Cow.class, DefaultCowEntity.class), new DefaultCowMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Horse.class, DefaultHorseEntity.class), new DefaultHorseMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Ocelot.class, DefaultOcelotEntity.class), new DefaultOcelotMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Wolf.class, DefaultWolfEntity.class), new DefaultWolfMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Rabbit.class, DefaultRabbitEntity.class), new DefaultRabbitMarshal());
        binaryTagMarshal.registerAdaptor(SheepColor.class, new DefaultSheepColorMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Sheep.class, DefaultSheepEntity.class), new DefaultSheepMarshal());
        binaryTagMarshal.registerAdaptor(VillagerType.class, new DefaultVillagerTypeMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Villager.class, DefaultVillagerEntity.class), new DefaultVillagerMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Enderman.class, DefaultEndermanEntity.class), new DefaultEndermanMarshal());
        binaryTagMarshal.registerAdaptor(CreeperState.class, new DefaultCreeperStateMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Creeper.class, DefaultCreeperEntity.class), new DefaultCreeperMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Ghast.class, DefaultGhastEntity.class), new DefaultGhastMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Slime.class, DefaultSlimeEntity.class), new DefaultSlimeMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(MagmaCube.class, DefaultMagmaCubeEntity.class), new DefaultMagmaCubeMarshal());
        binaryTagMarshal.registerAdaptor(SkeletonType.class, new DefaultSkeletonMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Skeleton.class, DefaultSkeletonEntity.class), new DefaultSkeletonMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Witch.class, DefaultWitchEntity.class), new DefaultWitchMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(IronGolem.class, DefaultIronGolemEntity.class), new DefaultIronGolemMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Wither.class, DefaultWitherEntity.class), new DefaultWitherMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Guardian.class, DefaultGuardianEntity.class), new DefaultGuardianMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Boat.class, DefaultBoatEntity.class), new DefaultBoatMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Minecart.class, DefaultMinecartEntity.class), new DefaultMinecartMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(FurnaceMinecart.class, DefaultFurnaceMinecartEntity.class), new DefaultFurnaceMinecartMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Item.class, DefaultItemEntity.class), new DefaultItemMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Firework.class, DefaultFireworkEntity.class), new DefaultFireworkMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(ItemFrame.class, DefaultItemFrameEntity.class), new DefaultItemFrameMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(EnderCrystal.class, DefaultEnderCrystalEntity.class), new DefaultEnderCrystalMarshal());
        binaryTagMarshal.registerAdaptor(GameMode.class, new DefaultGameModeMarshal());
        binaryTagMarshal.registerAdaptor(GameProfile.class, new DefaultGameProfileMarshal());
        binaryTagMarshal.registerAdaptor(Property.class, new DefaultPropertyMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Silverfish.class, DefaultSilverfishEntity.class), new DefaultSilverfishMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(GiantZombie.class, DefaultGiantZombieEntity.class), new DefaultGiantZombieMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(EnderDragon.class, DefaultEnderDragonEntity.class), new DefaultEnderDragonMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Squid.class, DefaultSquidEntity.class), new DefaultSquidMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Mooshroom.class, DefaultMooshroomEntity.class), new DefaultMooshroomMarshal());
        binaryTagMarshal.registerAdaptor(Arrays.asList(Snowman.class, DefaultSnowmanEntity.class), new DefaultSnowmanMarshal());

        eventLoopGroup.scheduleAtFixedRate(() ->
                dedicatedEnderChest.getPlayers().forEach(player ->
                        player.getEnderFrameSession().sendKeepAlive((int) System.currentTimeMillis())), 0, 1, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            dedicatedEnderChest.getWorldManager().getWorlds().forEach(worldBuffer -> {
                dedicatedEnderChest.getWorldManager().getWorldBufferIO().writeWorldLevel(worldBuffer.getWorldLevel(), new File(worldBuffer.getWorldFolder(), "level.dat"));
                worldBuffer.getChunkBuffers().forEach(worldBuffer::flushChunkBuffer);
            });
            eventLoopGroup.shutdownGracefully();
        }));

        new ServerBootstrap()
                .group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new EnderChestChannelInitializer())
                .localAddress("0.0.0.0", 25565)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .bind();
        logger.info("Done !");
    }

    public static void main(String[] args) {
        new EnderChest();
    }

    public EventLoopGroup getEventLoopGroup() {
        return eventLoopGroup;
    }

    public DedicatedEnderChest getDedicatedEnderChest() {
        return dedicatedEnderChest;
    }

}
