package eu.mshade.enderchest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.mshade.enderchest.emerald.Emerald;
import eu.mshade.enderchest.entity.*;
import eu.mshade.enderchest.entity.marshal.common.*;
import eu.mshade.enderchest.entity.marshal.entity.*;
import eu.mshade.enderchest.listener.*;
import eu.mshade.enderchest.protocol.listener.*;
import eu.mshade.enderchest.world.DefaultChunkBuffer;
import eu.mshade.enderchest.world.DefaultSectionBuffer;
import eu.mshade.enderchest.world.marshal.DefaultChunkMarshal;
import eu.mshade.enderchest.world.marshal.DefaultLocationMarshal;
import eu.mshade.enderchest.world.marshal.DefaultSectionMarshal;
import eu.mshade.enderchest.world.marshal.DefaultVectorMarshal;
import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.GameMode;
import eu.mshade.enderframe.entity.*;
import eu.mshade.enderframe.event.*;
import eu.mshade.enderframe.mojang.GameProfile;
import eu.mshade.enderframe.mojang.Property;
import eu.mshade.enderframe.mojang.chat.*;
import eu.mshade.enderframe.packetevent.*;
import eu.mshade.enderframe.world.*;
import eu.mshade.mwork.MWork;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;
import eu.mshade.mwork.event.EventBus;
import eu.mshade.mwork.event.EventFilter;
import eu.mshade.mwork.event.EventPriority;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class EnderChest {

    private final EventLoopGroup parentGroup = new NioEventLoopGroup();
    private final EventLoopGroup childGroup = new NioEventLoopGroup();
    private final DedicatedEnderChest dedicatedEnderChest;
    private final Logger logger = LoggerFactory.getLogger(EnderChest.class);
    private Emerald emerald;

    public EnderChest() {
        long start = System.currentTimeMillis();
        System.out.println("\n" +
                "███████╗███╗░░██╗██████╗░███████╗██████╗░░█████╗░██╗░░██╗███████╗░██████╗████████╗\n" +
                "██╔════╝████╗░██║██╔══██╗██╔════╝██╔══██╗██╔══██╗██║░░██║██╔════╝██╔════╝╚══██╔══╝\n" +
                "█████╗░░██╔██╗██║██║░░██║█████╗░░██████╔╝██║░░╚═╝███████║█████╗░░╚█████╗░░░░██║░░░\n" +
                "██╔══╝░░██║╚████║██║░░██║██╔══╝░░██╔══██╗██║░░██╗██╔══██║██╔══╝░░░╚═══██╗░░░██║░░░\n" +
                "███████╗██║░╚███║██████╔╝███████╗██║░░██║╚█████╔╝██║░░██║███████╗██████╔╝░░░██║░░░\n" +
                "╚══════╝╚═╝░░╚══╝╚═════╝░╚══════╝╚═╝░░╚═╝░╚════╝░╚═╝░░╚═╝╚══════╝╚═════╝░░░░╚═╝░░░");
        logger.info("Starting EnderChest");
        this.dedicatedEnderChest  = new DedicatedEnderChest(parentGroup);

        TextComponentSerializer textComponentSerializer = new TextComponentSerializer();
        ObjectMapper objectMapper = MWork.get().getObjectMapper();

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(TextComponentEntry.class, textComponentSerializer);
        simpleModule.addSerializer(TextComponent.class, textComponentSerializer);
        simpleModule.addSerializer(TextClickEvent.class, new TextClickEventSerializer());

        objectMapper.registerModule(simpleModule);


        EnderFrame enderFrame = EnderFrame.get();
        EventBus<PacketEvent> packetEventBus = enderFrame.getPacketEventBus();

        packetEventBus.subscribe(PacketHandshakeEvent.class, new PacketHandshakeListener(dedicatedEnderChest));
        packetEventBus.subscribe(ServerPingEvent.class, new ServerPingListener());
        packetEventBus.subscribe(ServerStatusEvent.class, new ServerStatusListener());
        packetEventBus.subscribe(PacketLoginEvent.class, new PacketLoginHandler(dedicatedEnderChest));
        packetEventBus.subscribe(PacketEncryptionEvent.class, new PacketEncryptionHandler(dedicatedEnderChest));
        packetEventBus.subscribe(PacketKeepAliveEvent.class, new PacketKeepAliveHandler(dedicatedEnderChest));
        packetEventBus.subscribe(PacketClientSettingsEvent.class, new PacketClientSettingsHandler());
        packetEventBus.subscribe(PacketChatMessageEvent.class, new PacketChatMessageHandler(dedicatedEnderChest));
        packetEventBus.subscribe(PacketFinallyJoinEvent.class, new PacketFinallyJoinHandler(dedicatedEnderChest));
        packetEventBus.subscribe(PacketQuitEvent.class, new PacketQuitHandler(dedicatedEnderChest));
        packetEventBus.subscribe(PacketEntityActionEvent.class, new PacketEntityActionHandler());
        packetEventBus.subscribe(PacketMoveEvent.class, new PacketMoveHandler(this));
        packetEventBus.subscribe(PacketLookEvent.class, new PacketLookHandler(this));
        packetEventBus.subscribe(PacketMoveAndLookEvent.class, new PacketMoveAndLookHandler(this));
        packetEventBus.subscribe(PacketMoveEvent.class, new PacketRequestChunkHandler(dedicatedEnderChest))
                .withEventFilter(EventFilter.DERIVE)
                .withEventPriority(EventPriority.LOW);

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

        binaryTagMarshal.registerAdaptor(GameMode.class, new DefaultGameModeMarshal());
        binaryTagMarshal.registerAdaptor(GameProfile.class, new DefaultGameProfileMarshal());
        binaryTagMarshal.registerAdaptor(Property.class, new DefaultPropertyMarshal());
        binaryTagMarshal.registerAdaptor(VillagerType.class, new DefaultVillagerTypeMarshal());
        binaryTagMarshal.registerAdaptor(Rotation.class, new DefaultRotationMarshal());
        binaryTagMarshal.registerAdaptor(Vector.class, new DefaultVectorMarshal());
        binaryTagMarshal.registerAdaptor(Location.class, new DefaultLocationMarshal());
        binaryTagMarshal.registerAdaptor(SkeletonType.class, new DefaultSkeletonMarshal());
        binaryTagMarshal.registerAdaptor(SheepColor.class, new DefaultSheepColorMarshal());
        binaryTagMarshal.registerAdaptor(CreeperState.class, new DefaultCreeperStateMarshal());

        binaryTagMarshal.registerAdaptor(SectionBuffer.class, new DefaultSectionMarshal())
                .registerSubTypes(DefaultSectionBuffer.class);
        binaryTagMarshal.registerAdaptor(ChunkBuffer.class, new DefaultChunkMarshal())
                .registerSubTypes(DefaultChunkBuffer.class);
        binaryTagMarshal.registerAdaptor(Zombie.class, new DefaultZombieMarshal())
                .registerSubTypes(DefaultZombieEntity.class);
        binaryTagMarshal.registerAdaptor(Player.class, new DefaultPlayerMarshal())
                .registerSubTypes(DefaultPlayerEntity.class);
        binaryTagMarshal.registerAdaptor(Blaze.class, new DefaultBlazeMarshal())
                .registerSubTypes(DefaultBlazeEntity.class);
        binaryTagMarshal.registerAdaptor(ArmorStand.class, new DefaultArmorStandMarshal())
                .registerSubTypes(DefaultArmorStandEntity.class);
        binaryTagMarshal.registerAdaptor(Arrow.class, new DefaultArrowMarshal())
                .registerSubTypes(DefaultArrowEntity.class);
        binaryTagMarshal.registerAdaptor(Ageable.class, new DefaultAgeableMarshal())
                .registerSubTypes(DefaultAgeableEntity.class);
        binaryTagMarshal.registerAdaptor(Rideable.class, new DefaultRideableMarshal())
                .registerSubTypes(DefaultRideableEntity.class);
        binaryTagMarshal.registerAdaptor(Pig.class, new DefaultPigMarshal())
                .registerSubTypes(DefaultPigEntity.class);
        binaryTagMarshal.registerAdaptor(Bat.class, new DefaultBatMarshal())
                .registerSubTypes(DefaultBatEntity.class);
        binaryTagMarshal.registerAdaptor(Spider.class, new DefaultSpiderMarshal())
                .registerSubTypes(DefaultSpiderEntity.class);
        binaryTagMarshal.registerAdaptor(CaveSpider.class, new DefaultCaveSpiderMarshal())
                .registerSubTypes(DefaultCaveSpiderEntity.class);
        binaryTagMarshal.registerAdaptor(Tameable.class, new DefaultTameableMarshal())
                .registerSubTypes(DefaultTameableEntity.class);
        binaryTagMarshal.registerAdaptor(Chicken.class, new DefaultChickenMarshal())
                .registerSubTypes(DefaultChickenEntity.class);
        binaryTagMarshal.registerAdaptor(Cow.class, new DefaultCowMarshal())
                .registerSubTypes(DefaultCowEntity.class);
        binaryTagMarshal.registerAdaptor(Horse.class, new DefaultHorseMarshal())
                .registerSubTypes(DefaultHorseEntity.class);
        binaryTagMarshal.registerAdaptor(Ocelot.class, new DefaultOcelotMarshal())
                .registerSubTypes(DefaultOcelotEntity.class);
        binaryTagMarshal.registerAdaptor(Wolf.class, new DefaultWolfMarshal())
                .registerSubTypes(DefaultWolfEntity.class);
        binaryTagMarshal.registerAdaptor(Rabbit.class, new DefaultRabbitMarshal())
                .registerSubTypes(DefaultRabbitEntity.class);
        binaryTagMarshal.registerAdaptor(Sheep.class, new DefaultSheepMarshal())
                .registerSubTypes(DefaultSheepEntity.class);
        binaryTagMarshal.registerAdaptor(Villager.class, new DefaultVillagerMarshal())
                .registerSubTypes(DefaultVillagerEntity.class);
        binaryTagMarshal.registerAdaptor(Enderman.class, new DefaultEndermanMarshal())
                .registerSubTypes(DefaultEndermanEntity.class);
        binaryTagMarshal.registerAdaptor(Creeper.class, new DefaultCreeperMarshal())
                .registerSubTypes(DefaultCreeperEntity.class);
        binaryTagMarshal.registerAdaptor(Ghast.class, new DefaultGhastMarshal())
                .registerSubTypes(DefaultGhastEntity.class);
        binaryTagMarshal.registerAdaptor(Slime.class, new DefaultSlimeMarshal())
                .registerSubTypes(DefaultSlimeEntity.class);
        binaryTagMarshal.registerAdaptor(MagmaCube.class, new DefaultMagmaCubeMarshal())
                .registerSubTypes(DefaultMagmaCubeEntity.class);
        binaryTagMarshal.registerAdaptor(Skeleton.class, new DefaultSkeletonMarshal())
                .registerSubTypes(DefaultSkeletonEntity.class);
        binaryTagMarshal.registerAdaptor(Witch.class, new DefaultWitchMarshal())
                .registerSubTypes(DefaultWitchEntity.class);
        binaryTagMarshal.registerAdaptor(IronGolem.class, new DefaultIronGolemMarshal())
                .registerSubTypes(DefaultIronGolemEntity.class);
        binaryTagMarshal.registerAdaptor(Wither.class, new DefaultWitherMarshal())
                .registerSubTypes(DefaultWitherEntity.class);
        binaryTagMarshal.registerAdaptor(Guardian.class, new DefaultGuardianMarshal())
                .registerSubTypes(DefaultGuardianEntity.class);
        binaryTagMarshal.registerAdaptor(Boat.class, new DefaultBoatMarshal())
                .registerSubTypes(DefaultBoatEntity.class);
        binaryTagMarshal.registerAdaptor(Minecart.class, new DefaultMinecartMarshal())
                .registerSubTypes(DefaultMinecartEntity.class);
        binaryTagMarshal.registerAdaptor(FurnaceMinecart.class, new DefaultFurnaceMinecartMarshal())
                .registerSubTypes(DefaultFurnaceMinecartEntity.class);
        binaryTagMarshal.registerAdaptor(Item.class, new DefaultItemMarshal())
                .registerSubTypes(DefaultItemEntity.class);
        binaryTagMarshal.registerAdaptor(Firework.class, new DefaultFireworkMarshal())
                .registerSubTypes(DefaultFireworkEntity.class);
        binaryTagMarshal.registerAdaptor(ItemFrame.class, new DefaultItemFrameMarshal())
                .registerSubTypes(DefaultItemFrameEntity.class);
        binaryTagMarshal.registerAdaptor(EnderCrystal.class, new DefaultEnderCrystalMarshal())
                .registerSubTypes(DefaultEnderCrystalEntity.class);
        binaryTagMarshal.registerAdaptor(Silverfish.class, new DefaultSilverfishMarshal())
                .registerSubTypes(DefaultSilverfishEntity.class);
        binaryTagMarshal.registerAdaptor(GiantZombie.class, new DefaultGiantZombieMarshal())
                .registerSubTypes(DefaultGiantZombieEntity.class);
        binaryTagMarshal.registerAdaptor(EnderDragon.class, new DefaultEnderDragonMarshal())
                .registerSubTypes(DefaultEnderDragonEntity.class);
        binaryTagMarshal.registerAdaptor(Squid.class, new DefaultSquidMarshal())
                .registerSubTypes(DefaultSquidEntity.class);
        binaryTagMarshal.registerAdaptor(Mooshroom.class, new DefaultMooshroomMarshal())
                .registerSubTypes(DefaultMooshroomEntity.class);
        binaryTagMarshal.registerAdaptor(Snowman.class, new DefaultSnowmanMarshal())
                .registerSubTypes(DefaultSnowmanEntity.class);


        parentGroup.scheduleAtFixedRate(() ->
                dedicatedEnderChest.getPlayers().forEach(player ->
                        player.getEnderFrameSession().sendKeepAlive((int) System.currentTimeMillis())), 0, 1, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            dedicatedEnderChest.getWorldManager().getWorlds().forEach(worldBuffer -> {
                dedicatedEnderChest.getWorldManager().getWorldBufferIO().writeWorldLevel(worldBuffer.getWorldLevel(), new File(worldBuffer.getWorldFolder(), "level.dat"));
                worldBuffer.getChunkBuffers().forEach(worldBuffer::flushChunkBuffer);
            });
            parentGroup.shutdownGracefully();
        }));

        this.emerald = new Emerald(parentGroup);

        ChannelFuture channelFuture = new ServerBootstrap()
                .group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new EnderChestChannelInitializer())
                .localAddress("0.0.0.0", 25565)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .bind();

        try {
            logger.info(String.valueOf(channelFuture.sync().channel()));
        } catch (InterruptedException e) {
            logger.error("", e);
        }

        logger.info(String.format("took (%d) ms", (System.currentTimeMillis() - start)));
        logger.info("done !");


    }

    public static void main(String[] args) {
        new EnderChest();
    }

    public EventLoopGroup getParentGroup() {
        return parentGroup;
    }

    public DedicatedEnderChest getDedicatedEnderChest() {
        return dedicatedEnderChest;
    }

    public Emerald getEmerald() {
        return emerald;
    }
}
