package eu.mshade.enderchest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.mshade.enderchest.emerald.Emerald;
import eu.mshade.enderchest.listener.*;
import eu.mshade.enderchest.marshal.common.*;
import eu.mshade.enderchest.marshal.metadata.MetadataKeyValueBinaryTagMarshal;
import eu.mshade.enderchest.marshal.world.*;
import eu.mshade.enderchest.protocol.ProtocolRepository;
import eu.mshade.enderchest.protocol.listener.*;
import eu.mshade.enderchest.world.ChunkSafeguard;
import eu.mshade.enderchest.world.DefaultChunkGenerator;
import eu.mshade.enderchest.world.SchematicLoader;
import eu.mshade.enderchest.world.WorldManager;
import eu.mshade.enderframe.Agent;
import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.GameMode;
import eu.mshade.enderframe.entity.CreeperState;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.entity.VillagerType;
import eu.mshade.enderframe.event.*;
import eu.mshade.enderframe.item.Material;
import eu.mshade.enderframe.item.MaterialKey;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.enderframe.mojang.GameProfile;
import eu.mshade.enderframe.mojang.Property;
import eu.mshade.enderframe.mojang.chat.*;
import eu.mshade.enderframe.packetevent.*;
import eu.mshade.enderframe.protocol.MinecraftEncryption;
import eu.mshade.enderframe.sound.Sound;
import eu.mshade.enderframe.sound.SoundKey;
import eu.mshade.enderframe.tick.TickBus;
import eu.mshade.enderframe.world.*;
import eu.mshade.enderframe.world.block.Block;
import eu.mshade.enderframe.world.block.BlockFace;
import eu.mshade.enderframe.world.block.FaceBlockMetadata;
import eu.mshade.enderframe.world.block.PoweredBlockMetadata;
import eu.mshade.enderframe.world.chunk.Chunk;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

public class EnderChest {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnderChest.class);
    private final EventLoopGroup parentGroup;
    private final EventLoopGroup childGroup;
    private final Queue<Player> players = new ConcurrentLinkedQueue<>();
    private final MinecraftEncryption minecraftEncryption = new MinecraftEncryption();
    private final ProtocolRepository protocolRepository = new ProtocolRepository();
    private final WorldManager worldManager;
    private final TickBus tickBus = new TickBus(20);
    private Emerald emerald;

    public EnderChest() {
        long start = System.currentTimeMillis();
        System.out.println("""
                ███████╗███╗░░██╗██████╗░███████╗██████╗░░█████╗░██╗░░██╗███████╗░██████╗████████╗
                ██╔════╝████╗░██║██╔══██╗██╔════╝██╔══██╗██╔══██╗██║░░██║██╔════╝██╔════╝╚══██╔══╝
                █████╗░░██╔██╗██║██║░░██║█████╗░░██████╔╝██║░░╚═╝███████║█████╗░░╚█████╗░░░░██║░░░
                ██╔══╝░░██║╚████║██║░░██║██╔══╝░░██╔══██╗██║░░██╗██╔══██║██╔══╝░░░╚═══██╗░░░██║░░░
                ███████╗██║░╚███║██████╔╝███████╗██║░░██║╚█████╔╝██║░░██║███████╗██████╔╝░░░██║░░░
                ╚══════╝╚═╝░░╚══╝╚═════╝░╚══════╝╚═╝░░╚═╝░╚════╝░╚═╝░░╚═╝╚══════╝╚═════╝░░░░╚═╝░░░""");
        LOGGER.info("Starting EnderChest");

        BinaryTagDriver binaryTagDriver = MWork.get().getBinaryTagDriver();

        parentGroup = new NioEventLoopGroup();
        childGroup = new NioEventLoopGroup();

        this.protocolRepository.register(new EndermanProtocol());

        TextComponentSerializer textComponentSerializer = new TextComponentSerializer();
        ObjectMapper objectMapper = MWork.get().getObjectMapper();

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(TextComponentEntry.class, textComponentSerializer);
        simpleModule.addSerializer(TextComponent.class, textComponentSerializer);
        simpleModule.addSerializer(TextClickEvent.class, new TextClickEventSerializer());

        objectMapper.registerModule(simpleModule);

        EnderFrame enderFrame = EnderFrame.get();
        EventBus<PacketEvent> packetEventBus = enderFrame.getPacketEventBus();

        packetEventBus.subscribe(PacketHandshakeEvent.class, new PacketHandshakeListener(this));
        packetEventBus.subscribe(ServerPingEvent.class, new ServerPingListener());
        packetEventBus.subscribe(ServerStatusEvent.class, new ServerStatusListener());
        packetEventBus.subscribe(PacketLoginEvent.class, new PacketLoginHandler(this));
        packetEventBus.subscribe(PacketEncryptionEvent.class, new PacketEncryptionHandler(this));
        packetEventBus.subscribe(PacketKeepAliveEvent.class, new PacketKeepAliveHandler(this));
        packetEventBus.subscribe(PacketClientSettingsEvent.class, new PacketClientSettingsHandler());
        packetEventBus.subscribe(PacketChatMessageEvent.class, new PacketChatMessageHandler(this));
        packetEventBus.subscribe(PacketFinallyJoinEvent.class, new PacketFinallyJoinHandler(this));
        packetEventBus.subscribe(PacketEntityActionEvent.class, new PacketEntityActionHandler());
        packetEventBus.subscribe(PacketMoveEvent.class, new PacketMoveHandler());
        packetEventBus.subscribe(PacketLookEvent.class, new PacketLookHandler());
        packetEventBus.subscribe(PacketMoveAndLookEvent.class, new PacketMoveAndLookHandler());
        packetEventBus.subscribe(PacketMoveEvent.class, new PacketRequestChunkHandler())
                .withEventFilter(EventFilter.DERIVE)
                .withEventPriority(EventPriority.LOW);

        packetEventBus.subscribe(PacketToggleFlyingEvent.class, new PacketToggleFlyingListener());
        packetEventBus.subscribe(PacketBlockPlaceEvent.class, new PacketBlockPlaceListener());
        packetEventBus.subscribe(PacketPlayerDiggingEvent.class, new PacketPlayerDiggingListener());
        packetEventBus.subscribe(PacketCloseInventoryEvent.class, new PacketCloseInventoryHandler());
        packetEventBus.subscribe(PacketClickInventoryEvent.class, new PacketClickInventoryHandler());

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

        enderFrameEventBus.subscribe(PlayerQuitEvent.class, new PlayerQuitHandler(this));

        MetadataKeyValueBinaryTagMarshal metadataKeyValueBinaryTagMarshal = new MetadataKeyValueBinaryTagMarshal(binaryTagDriver);

        binaryTagDriver.registerMarshal(GameMode.class, new DefaultGameModeMarshal());
        binaryTagDriver.registerMarshal(GameProfile.class, new DefaultGameProfileMarshal());
        binaryTagDriver.registerMarshal(Property.class, new DefaultPropertyMarshal());
        binaryTagDriver.registerMarshal(VillagerType.class, new DefaultVillagerTypeMarshal());
        binaryTagDriver.registerMarshal(Rotation.class, new DefaultRotationMarshal());
        binaryTagDriver.registerMarshal(CreeperState.class, new DefaultCreeperStateMarshal());
        binaryTagDriver.registerMarshal(Difficulty.class, new DifficultyBinaryTagMarshal());
        binaryTagDriver.registerMarshal(Dimension.class, new DimensionBinaryTagMarshal());
        binaryTagDriver.registerMarshal(LevelType.class, new LevelTypeBinaryTagMarshal());


        binaryTagDriver.registerDynamicMarshal(metadataKeyValueBinaryTagMarshal);
        binaryTagDriver.registerDynamicMarshal(new WorldBinaryTagMarshal());
        binaryTagDriver.registerDynamicMarshal(new ChunkBinaryTagMarshal());
        binaryTagDriver.registerDynamicMarshal(new DefaultVectorMarshal());
        binaryTagDriver.registerDynamicMarshal(new LocationBinaryTagMarshal());
        binaryTagDriver.registerDynamicMarshal(new UniqueIdBinaryTagMarshal());
        binaryTagDriver.registerDynamicMarshal(new PaletteBinaryTagMarshal(binaryTagDriver));
        binaryTagDriver.registerDynamicMarshal(new SectionBinaryTagMarshal(binaryTagDriver));



        this.worldManager = new WorldManager(binaryTagDriver, this);

        World world = this.worldManager.createWorld("world", metadataKeyValueBucket -> {
            metadataKeyValueBucket.setMetadataKeyValue(new SeedWorldMetadata(-4975988339999789512L));
            metadataKeyValueBucket.setMetadataKeyValue(new LevelTypeWorldMetadata(LevelType.DEFAULT));
            metadataKeyValueBucket.setMetadataKeyValue(new DimensionWorldMetadata(Dimension.OVERWORLD));
            metadataKeyValueBucket.setMetadataKeyValue(new DifficultyWorldMetadata(Difficulty.NORMAL));
        });
        world.setChunkGenerator(new DefaultChunkGenerator(world));

        Thread threadTickBus = new Thread(tickBus, "TickBus");
        threadTickBus.start();
        LOGGER.info("Starting " + threadTickBus);


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.warn("Beginning save of server don't close the console !");
            worldManager.getChunkSafeguard().stopSafeguard();
            worldManager.getWorlds().forEach(w -> {
                LOGGER.info("Saving world " + w.getName());
                w.saveWorld();
                w.getChunks().forEach(chunkCompletableFuture -> {
                    w.saveChunk(chunkCompletableFuture.join());
                });
                // log number of chunks saved in the world
                LOGGER.info("Saved " + w.getChunks().size() + " chunks in world " + w.getName());
                w.getRegionBinaryTagPoets().forEach(binaryTagPoet -> {
                    if (binaryTagPoet.getCompoundSectionIndex().consume()) binaryTagPoet.writeCompoundSectionIndex();
                });
            });
            LOGGER.info("Worlds saved");
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
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
            LOGGER.info(String.valueOf(channelFuture.sync().channel()));
        } catch (InterruptedException e) {
            LOGGER.error("", e);
        }


        LOGGER.info("Done in {} ms !", (System.currentTimeMillis() - start));


/*
        try {
            Chunk chunk = world.getChunk(0, 0).get();
            Agent agentTest = Agent.from("AGENT_TEST");
            agentTest.joinWatch(chunk);

            Block block = Material.OAK_BUTTON.toBlock();
            MetadataKeyValueBucket metadataKeyValueBucket = block.getMetadataKeyValueBucket();
            BlockFace[] blockFaces = BlockFace.values();


            parentGroup.scheduleAtFixedRate(()-> {
                metadataKeyValueBucket.setMetadataKeyValue(new FaceBlockMetadata(blockFaces[ThreadLocalRandom.current().nextInt(blockFaces.length)]));
                metadataKeyValueBucket.setMetadataKeyValue(new PoweredBlockMetadata(ThreadLocalRandom.current().nextBoolean()));

                chunk.getWatching().forEach(agent -> {
                    if (agent instanceof Player player) {
                        player.getSessionWrapper().sendBlockChange(new Vector(7, 53, 7), block);
                    }
                });

            },0, 600, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
*/



//        SchematicLoader.placeSchematic(world, this.getClass().getClassLoader().getResourceAsStream("./spawn-e1930.schematic"), new Vector(0, 0, 0));
///*        SchematicLoader.placeSchematic(world, this.getClass().getClassLoader().getResourceAsStream("./spawn-e1930.schematic"), new Vector(130, 0, 0));
//        SchematicLoader.placeSchematic(world, this.getClass().getClassLoader().getResourceAsStream("./WaitingLobby1.schematic"), new Vector(-1577, 1, -95));

/*        parentGroup.scheduleAtFixedRate(()->{
            System.out.println(String.valueOf(tickBus.getTPS()).replace(".", ","));
        }, 0, 1, TimeUnit.SECONDS);*/






/*        parentGroup.execute(()->{
            long startGenMap = System.currentTimeMillis();
            List<CompletableFuture<Chunk>> ask = new ArrayList<>();
            for (int x = 0; x < 250; x++) {
                for (int z = 0; z < 250; z++) {
                    ask.add(world.getChunk(x, z));

                }
            }
            try {
                Void unused = CompletableFuture.allOf(ask.toArray(new CompletableFuture[0])).get();
                LOGGER.info("Done GenMap in {} ms", (System.currentTimeMillis()-startGenMap));
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("", e);
            }

        });*/



    }

    public static void main(String[] args) {
        new EnderChest();
    }

    public EventLoopGroup getParentGroup() {
        return parentGroup;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void removePlayer(Player player) {
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
