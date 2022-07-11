package eu.mshade.enderchest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import eu.mshade.enderchest.emerald.Emerald;
import eu.mshade.enderchest.listener.*;
import eu.mshade.enderchest.marshal.common.*;
import eu.mshade.enderchest.marshal.metadata.MetadataKeyValueBinaryTagMarshal;
import eu.mshade.enderchest.marshal.metadata.WorldMetadataKeyValueBucket;
import eu.mshade.enderchest.marshal.world.*;
import eu.mshade.enderchest.protocol.ProtocolRepository;
import eu.mshade.enderchest.protocol.listener.*;
import eu.mshade.enderchest.world.DefaultChunkGenerator;
import eu.mshade.enderchest.world.WorldManager;
import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.GameMode;
import eu.mshade.enderframe.entity.CreeperState;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.entity.VillagerType;
import eu.mshade.enderframe.event.*;
import eu.mshade.enderframe.metadata.world.WorldMetadataType;
import eu.mshade.enderframe.mojang.GameProfile;
import eu.mshade.enderframe.mojang.Property;
import eu.mshade.enderframe.mojang.chat.*;
import eu.mshade.enderframe.packetevent.*;
import eu.mshade.enderframe.protocol.MinecraftEncryption;
import eu.mshade.enderframe.tick.TickBus;
import eu.mshade.enderframe.world.*;
import eu.mshade.enderframe.world.metadata.DifficultyWorldMetadata;
import eu.mshade.enderframe.world.metadata.DimensionWorldMetadata;
import eu.mshade.enderframe.world.metadata.LevelTypeWorldMetadata;
import eu.mshade.enderframe.world.metadata.SeedWorldMetadata;
import eu.mshade.enderman.EndermanProtocol;
import eu.mshade.mwork.MWork;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class EnderChest extends AbstractModule {

    private final Logger logger = LoggerFactory.getLogger(EnderChest.class);
    private final EventLoopGroup parentGroup = new NioEventLoopGroup();
    private final EventLoopGroup childGroup = new NioEventLoopGroup();
    private final Queue<Player> players = new ConcurrentLinkedQueue<>();
    private final MinecraftEncryption minecraftEncryption = new MinecraftEncryption();
    private final ProtocolRepository protocolRepository = new ProtocolRepository();
    private final WorldManager worldManager;
    private final TickBus tickBus = new TickBus(20);
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

        BinaryTagDriver binaryTagDriver = MWork.get().getBinaryTagDriver();

        this.protocolRepository.register(new EndermanProtocol());



        Injector injector = Guice.createInjector(this);

        TextComponentSerializer textComponentSerializer = new TextComponentSerializer();
        ObjectMapper objectMapper = MWork.get().getObjectMapper();

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(TextComponentEntry.class, textComponentSerializer);
        simpleModule.addSerializer(TextComponent.class, textComponentSerializer);
        simpleModule.addSerializer(TextClickEvent.class, new TextClickEventSerializer());

        objectMapper.registerModule(simpleModule);

        EnderFrame enderFrame = EnderFrame.get();
        EventBus<PacketEvent> packetEventBus = enderFrame.getPacketEventBus();

        packetEventBus.subscribe(PacketHandshakeEvent.class, injector.getInstance(PacketHandshakeListener.class));
        packetEventBus.subscribe(ServerPingEvent.class, injector.getInstance(ServerPingListener.class));
        packetEventBus.subscribe(ServerStatusEvent.class, injector.getInstance(ServerStatusListener.class));
        packetEventBus.subscribe(PacketLoginEvent.class, injector.getInstance(PacketLoginHandler.class));
        packetEventBus.subscribe(PacketEncryptionEvent.class, injector.getInstance(PacketEncryptionHandler.class));
        packetEventBus.subscribe(PacketKeepAliveEvent.class, injector.getInstance(PacketKeepAliveHandler.class));
        packetEventBus.subscribe(PacketClientSettingsEvent.class, injector.getInstance(PacketClientSettingsHandler.class));
        packetEventBus.subscribe(PacketChatMessageEvent.class, injector.getInstance(PacketChatMessageHandler.class));
        packetEventBus.subscribe(PacketFinallyJoinEvent.class, injector.getInstance(PacketFinallyJoinHandler.class));
        packetEventBus.subscribe(PacketEntityActionEvent.class,injector.getInstance(PacketEntityActionHandler.class));
        packetEventBus.subscribe(PacketMoveEvent.class, injector.getInstance(PacketMoveHandler.class));
        packetEventBus.subscribe(PacketLookEvent.class, injector.getInstance(PacketLookHandler.class));
        packetEventBus.subscribe(PacketMoveAndLookEvent.class, injector.getInstance(PacketMoveAndLookHandler.class));
        packetEventBus.subscribe(PacketMoveEvent.class,injector.getInstance(PacketRequestChunkHandler.class))
                .withEventFilter(EventFilter.DERIVE)
                .withEventPriority(EventPriority.LOW);

        packetEventBus.subscribe(PacketToggleFlyingEvent.class, new PacketToggleFlyingListener());
        packetEventBus.subscribe(PacketBlockPlaceEvent.class, new PacketBlockPlaceListener());
        packetEventBus.subscribe(PacketPlayerDiggingEvent.class, new PacketPlayerDiggingListener());

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

        enderFrameEventBus.subscribe(PlayerQuitEvent.class, injector.getInstance(PlayerQuitHandler.class));



        binaryTagDriver.registerMarshal(GameMode.class, new DefaultGameModeMarshal());
        binaryTagDriver.registerMarshal(GameProfile.class, new DefaultGameProfileMarshal());
        binaryTagDriver.registerMarshal(Property.class, new DefaultPropertyMarshal());
        binaryTagDriver.registerMarshal(VillagerType.class, new DefaultVillagerTypeMarshal());
        binaryTagDriver.registerMarshal(Rotation.class, new DefaultRotationMarshal());
        binaryTagDriver.registerMarshal(CreeperState.class, new DefaultCreeperStateMarshal());
        binaryTagDriver.registerMarshal(Difficulty.class, new DifficultyBinaryTagMarshal());
        binaryTagDriver.registerMarshal(Dimension.class, new DimensionBinaryTagMarshal());
        binaryTagDriver.registerMarshal(LevelType.class, new LevelTypeBinaryTagMarshal());


        binaryTagDriver.registerDynamicMarshal(new WorldBinaryTagMarshal());
        binaryTagDriver.registerDynamicMarshal(new SectionBinaryTagMarshal());
        binaryTagDriver.registerDynamicMarshal(new ChunkBinaryTagMarshal());
        binaryTagDriver.registerDynamicMarshal(new DefaultVectorMarshal());
        binaryTagDriver.registerDynamicMarshal(new LocationBinaryTagMarshal());

        MetadataKeyValueBinaryTagMarshal metadataKeyValueBinaryTagMarshal = new MetadataKeyValueBinaryTagMarshal();
        metadataKeyValueBinaryTagMarshal.registerMetadataKeValueBucketManager(WorldMetadataType.class, new WorldMetadataKeyValueBucket());

        binaryTagDriver.registerDynamicMarshal(metadataKeyValueBinaryTagMarshal);

        this.worldManager = new WorldManager(binaryTagDriver, this);

        World world = this.worldManager.createWorld("world", metadataKeyValueBucket -> {
            metadataKeyValueBucket.setMetadataKeyValue(new SeedWorldMetadata(-4975988339999789512L));
            metadataKeyValueBucket.setMetadataKeyValue(new LevelTypeWorldMetadata(LevelType.DEFAULT));
            metadataKeyValueBucket.setMetadataKeyValue(new DimensionWorldMetadata(Dimension.OVERWORLD));
            metadataKeyValueBucket.setMetadataKeyValue(new DifficultyWorldMetadata(Difficulty.NORMAL));
        });
        world.setChunkGenerator(new DefaultChunkGenerator(world));

        Thread thread = new Thread(tickBus, "TickBus");
        thread.start();
        // parentGroup.scheduleAtFixedRate(tickBus, 0, 1000/200, TimeUnit.MILLISECONDS);
        logger.info("starting TickBus");













        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            worldManager.getWorlds().forEach(worldBuffer -> {
                //worldManager.getWorldBufferIO().writeWorldLevel(worldBuffer.getWorldLevel(), new File(worldBuffer.getWorldFolder(), "level.dat"));
                worldBuffer.getChunks().forEach(worldBuffer::flushChunk);
                worldBuffer.getRegionBinaryTagPoets().forEach(binaryTagPoet -> {
                    if (binaryTagPoet.getCompoundSectionIndex().consume()) {
                        binaryTagPoet.writeCompoundSectionIndex();
                    }
                });
            });
            parentGroup.shutdownGracefully();
        }));

        //this.emerald = new Emerald(parentGroup);

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

        /*
        parentGroup.scheduleAtFixedRate(()->{
            System.out.println(String.valueOf(tickBus.getTPS()).replace(".", ","));
        }, 0, 1, TimeUnit.SECONDS);

         */

        /**
        parentGroup.execute(()->{
            long startGenMap = System.currentTimeMillis();
            for (int x = 0; x < 1000; x++) {
                for (int z = 0; z < 1000; z++) {
                    world.getChunkBuffer(x, z);
                }
            }

            System.out.println("Done GenMap in "+(System.currentTimeMillis()-startGenMap)+" ms");
        });
         **/


    }

    public static void main(String[] args) {
        new EnderChest();
    }

    @Override
    protected void configure() {
        bind(EnderChest.class).toInstance(this);
    }

    public EventLoopGroup getParentGroup() {
        return parentGroup;
    }

    public void addPlayer(Player player){
        this.players.add(player);
    }

    public void removePlayer(Player player){
        this.players.remove(player);
    }

    public Queue<Player> getPlayers() {
        return players;
    }

    public ProtocolRepository getProtocolRepository() {
        return protocolRepository;
    }

    public MinecraftEncryption getMinecraftEncryption() {
        return minecraftEncryption;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public Emerald getEmerald() {
        return emerald;
    }

    public TickBus getTickBus() {
        return tickBus;
    }
}
