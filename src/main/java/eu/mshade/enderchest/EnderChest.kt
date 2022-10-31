package eu.mshade.enderchest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import eu.mshade.enderchest.listener.*
import eu.mshade.enderchest.marshal.common.*
import eu.mshade.enderchest.marshal.metadata.MetadataKeyValueBinaryTagMarshal
import eu.mshade.enderchest.marshal.world.*
import eu.mshade.enderchest.protocol.ProtocolRepository
import eu.mshade.enderchest.protocol.listener.*
import eu.mshade.enderchest.world.DefaultChunkGenerator
import eu.mshade.enderchest.world.WorldManager
import eu.mshade.enderframe.EnderFrame
import eu.mshade.enderframe.GameMode
import eu.mshade.enderframe.entity.CreeperState
import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.entity.VillagerType
import eu.mshade.enderframe.event.*
import eu.mshade.enderframe.item.Material
import eu.mshade.enderframe.item.MaterialKey.DefaultMaterialKey
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket
import eu.mshade.enderframe.mojang.GameProfile
import eu.mshade.enderframe.mojang.NamespacedKey
import eu.mshade.enderframe.mojang.Property
import eu.mshade.enderframe.mojang.chat.*
import eu.mshade.enderframe.packetevent.*
import eu.mshade.enderframe.protocol.MinecraftEncryption
import eu.mshade.enderframe.tick.TickBus
import eu.mshade.enderframe.world.*
import eu.mshade.enderframe.world.chunk.Chunk
import eu.mshade.enderframe.world.metadata.DifficultyWorldMetadata
import eu.mshade.enderframe.world.metadata.DimensionWorldMetadata
import eu.mshade.enderframe.world.metadata.LevelTypeWorldMetadata
import eu.mshade.enderframe.world.metadata.SeedWorldMetadata
import eu.mshade.enderman.EndermanProtocol
import eu.mshade.mwork.MWork
import eu.mshade.mwork.binarytag.poet.BinaryTagPoet
import eu.mshade.mwork.event.EventFilter
import eu.mshade.mwork.event.EventPriorities
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

class EnderChest {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(EnderChest::class.java)
    }

    val parentGroup: EventLoopGroup
    private val childGroup: EventLoopGroup

    val players: Queue<Player> = ConcurrentLinkedQueue()
    val minecraftEncryption = MinecraftEncryption()
    val protocolRepository = ProtocolRepository()
    val worldManager: WorldManager
    val tickBus = TickBus(20)


    init {
        val start = System.currentTimeMillis()
        println("\n" +
                "███████╗███╗░░██╗██████╗░███████╗██████╗░░█████╗░██╗░░██╗███████╗░██████╗████████╗\n" +
                "██╔════╝████╗░██║██╔══██╗██╔════╝██╔══██╗██╔══██╗██║░░██║██╔════╝██╔════╝╚══██╔══╝\n" +
                "█████╗░░██╔██╗██║██║░░██║█████╗░░██████╔╝██║░░╚═╝███████║█████╗░░╚█████╗░░░░██║░░░\n" +
                "██╔══╝░░██║╚████║██║░░██║██╔══╝░░██╔══██╗██║░░██╗██╔══██║██╔══╝░░░╚═══██╗░░░██║░░░\n" +
                "███████╗██║░╚███║██████╔╝███████╗██║░░██║╚█████╔╝██║░░██║███████╗██████╔╝░░░██║░░░\n" +
                "╚══════╝╚═╝░░╚══╝╚═════╝░╚══════╝╚═╝░░╚═╝░╚════╝░╚═╝░░╚═╝╚══════╝╚═════╝░░░░╚═╝░░░")
        LOGGER.info("Starting EnderChest")
        val binaryTagDriver = MWork.get().binaryTagDriver
        parentGroup = NioEventLoopGroup()
        childGroup = NioEventLoopGroup()
        protocolRepository.register(EndermanProtocol())
        val textComponentSerializer = TextComponentSerializer()
        val objectMapper = MWork.get().objectMapper
        val simpleModule = SimpleModule()
        simpleModule.addSerializer(TextComponentEntry::class.java, textComponentSerializer)
        simpleModule.addSerializer(TextComponent::class.java, textComponentSerializer)
        simpleModule.addSerializer(TextClickEvent::class.java, TextClickEventSerializer())
        objectMapper.registerModule(simpleModule)
        val enderFrame = EnderFrame.get()

        val packetEventBus = enderFrame.packetEventBus
        packetEventBus.subscribe(PacketHandshakeEvent::class.java, PacketHandshakeListener(this))
        packetEventBus.subscribe(ServerPingEvent::class.java, ServerPingListener())
        packetEventBus.subscribe(ServerStatusEvent::class.java, ServerStatusListener())
        packetEventBus.subscribe(PacketLoginEvent::class.java, PacketLoginHandler(this))
        packetEventBus.subscribe(PacketEncryptionEvent::class.java, PacketEncryptionHandler(this))
        packetEventBus.subscribe(PacketKeepAliveEvent::class.java, PacketKeepAliveHandler(this))
        packetEventBus.subscribe(PacketClientSettingsEvent::class.java, PacketClientSettingsHandler())
        packetEventBus.subscribe(PacketChatMessageEvent::class.java, PacketChatMessageHandler(this))
        packetEventBus.subscribe(PacketFinallyJoinEvent::class.java, PacketFinallyJoinHandler(this))
        packetEventBus.subscribe(PacketEntityActionEvent::class.java, PacketEntityActionHandler())
        packetEventBus.subscribe(PacketMoveEvent::class.java, PacketMoveHandler())
        packetEventBus.subscribe(PacketLookEvent::class.java, PacketLookHandler())
        packetEventBus.subscribe(PacketMoveAndLookEvent::class.java, PacketMoveAndLookHandler())
        packetEventBus.subscribe(
            PacketMoveEvent::class.java,
            PacketRequestChunkHandler(),
            eventFilter = EventFilter.DERIVE,
            eventPriority = EventPriorities.LOW
        )
        packetEventBus.subscribe(PacketToggleFlyingEvent::class.java, PacketToggleFlyingListener())
        packetEventBus.subscribe(PacketBlockPlaceEvent::class.java, PacketBlockPlaceListener())
        packetEventBus.subscribe(PacketPlayerDiggingEvent::class.java, PacketPlayerDiggingListener())
        packetEventBus.subscribe(PacketCloseInventoryEvent::class.java, PacketCloseInventoryHandler())
        packetEventBus.subscribe(PacketClickInventoryEvent::class.java, PacketClickInventoryHandler())

        val enderFrameEventBus = enderFrame.enderFrameEventBus
        enderFrameEventBus.subscribe(EntityUnseeEvent::class.java, EntityUnseeHandler())
        enderFrameEventBus.subscribe(EntitySeeEvent::class.java, EntitySeeHandler())
        enderFrameEventBus.subscribe(ChunkSeeEvent::class.java, ChunkSeeHandler())
        enderFrameEventBus.subscribe(ChunkUnseeEvent::class.java, ChunkUnseeHandler())
        enderFrameEventBus.subscribe(EntityMoveEvent::class.java, EntityMoveHandler())
        enderFrameEventBus.subscribe(EntityTeleportEvent::class.java, EntityTeleportHandler())
        enderFrameEventBus.subscribe(EntityChunkChangeEvent::class.java, EntityChunkChangeHandler())
        enderFrameEventBus.subscribe(ChunkUnloadEvent::class.java, ChunkUnloadHandler())
        enderFrameEventBus.subscribe(ChunkLoadEvent::class.java, ChunkLoadHandler())
        enderFrameEventBus.subscribe(WatchdogSeeEvent::class.java, WatchdogSeeHandler())
        enderFrameEventBus.subscribe(WatchdogUnseeEvent::class.java, WatchdogUnseeHandler())
        enderFrameEventBus.subscribe(PlayerQuitEvent::class.java, PlayerQuitHandler(this))

        val metadataKeyValueBinaryTagMarshal = MetadataKeyValueBinaryTagMarshal(binaryTagDriver)
        binaryTagDriver.registerMarshal(GameMode::class.java, DefaultGameModeMarshal())
        binaryTagDriver.registerMarshal(GameProfile::class.java, DefaultGameProfileMarshal())
        binaryTagDriver.registerMarshal(Property::class.java, DefaultPropertyMarshal())
        binaryTagDriver.registerMarshal(VillagerType::class.java, DefaultVillagerTypeMarshal())
        binaryTagDriver.registerMarshal(Rotation::class.java, DefaultRotationMarshal())
        binaryTagDriver.registerMarshal(CreeperState::class.java, DefaultCreeperStateMarshal())
        binaryTagDriver.registerMarshal(Difficulty::class.java, DifficultyBinaryTagMarshal())
        binaryTagDriver.registerMarshal(Dimension::class.java, DimensionBinaryTagMarshal())
        binaryTagDriver.registerMarshal(LevelType::class.java, LevelTypeBinaryTagMarshal())

        binaryTagDriver.registerDynamicMarshal(metadataKeyValueBinaryTagMarshal)
        binaryTagDriver.registerDynamicMarshal(WorldBinaryTagMarshal())
        binaryTagDriver.registerDynamicMarshal(ChunkBinaryTagMarshal())
        binaryTagDriver.registerDynamicMarshal(DefaultVectorMarshal())
        binaryTagDriver.registerDynamicMarshal(LocationBinaryTagMarshal())
        binaryTagDriver.registerDynamicMarshal(UniqueIdBinaryTagMarshal())
        binaryTagDriver.registerDynamicMarshal(PaletteBinaryTagMarshal(binaryTagDriver))
        binaryTagDriver.registerDynamicMarshal(SectionBinaryTagMarshal(binaryTagDriver))

        // Register materials id
        val mapper = ObjectMapper()
        val materialsId = mapper.readTree(this::class.java.getResourceAsStream("materials.json"))
        materialsId.fields().forEach { (key, value) ->
            val material = Material.fromNamespacedKey(NamespacedKey.fromString(key)) as DefaultMaterialKey
            material.id = value.asInt()
            Material.registerMaterialKey(material)
        }

        worldManager = WorldManager(binaryTagDriver, this)
        val world = worldManager.createWorld("world") { metadataKeyValueBucket: MetadataKeyValueBucket ->
            metadataKeyValueBucket.setMetadataKeyValue(SeedWorldMetadata(-4975988339999789512L))
            metadataKeyValueBucket.setMetadataKeyValue(LevelTypeWorldMetadata(LevelType.DEFAULT))
            metadataKeyValueBucket.setMetadataKeyValue(DimensionWorldMetadata(Dimension.OVERWORLD))
            metadataKeyValueBucket.setMetadataKeyValue(DifficultyWorldMetadata(Difficulty.NORMAL))
        }
        world.chunkGenerator = DefaultChunkGenerator(world)

        val threadTickBus = Thread(tickBus, "TickBus")
        threadTickBus.start()

        LOGGER.info("Starting $threadTickBus")

        Runtime.getRuntime().addShutdownHook(Thread {
            LOGGER.warn("Beginning save of server don't close the console !")
            worldManager.chunkSafeguard.stopSafeguard()
            worldManager.worlds.forEach(Consumer { w: World ->
                LOGGER.info("Saving world " + w.name)
                w.saveWorld()
                w.chunks.forEach(Consumer { chunkCompletableFuture: CompletableFuture<Chunk?> ->
                    w.saveChunk(
                        chunkCompletableFuture.join()
                    )
                })
                // log number of chunks saved in the world
                LOGGER.info("Saved " + w.chunks.size + " chunks in world " + w.name)
                w.regionBinaryTagPoets.forEach(Consumer { binaryTagPoet: BinaryTagPoet -> if (binaryTagPoet.compoundSectionIndex.consume()) binaryTagPoet.writeCompoundSectionIndex() })
            })
            LOGGER.info("Worlds saved")
            parentGroup.shutdownGracefully()
            childGroup.shutdownGracefully()
        })

        //this.emerald = new Emerald(parentGroup);
        val channelFuture = ServerBootstrap()
            .group(parentGroup, childGroup)
            .channel(NioServerSocketChannel::class.java)
            .childHandler(EnderChestChannelInitializer())
            .localAddress("0.0.0.0", 25565)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .bind()
        try {
            LOGGER.info(channelFuture.sync().channel().toString())
        } catch (e: InterruptedException) {
            LOGGER.error("", e)
        }
        LOGGER.info("Done in {} ms !", System.currentTimeMillis() - start)


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

        //print length of chunk of the worlds
        parentGroup.scheduleAtFixedRate({
            worldManager.worlds.forEach(Consumer { w: World ->
                LOGGER.info("World " + w.name + " has " + w.chunks.size + " chunks")
            })
        }, 0, 1, TimeUnit.SECONDS)

    }

    fun addPlayer(player: Player) {
        players.add(player)
    }


    fun removePlayer(player: Player) {
        players.remove(player)
    }
}

fun main() {
    EnderChest()
}