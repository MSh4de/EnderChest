package eu.mshade.enderchest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import eu.mshade.axolotl.Axolotl
import eu.mshade.axolotl.event.ChatMessageAxolotlEvent
import eu.mshade.axolotl.event.HandshakeAxolotlEvent
import eu.mshade.axolotl.protocol.AxolotlProtocolRepository
import eu.mshade.enderchest.axolotl.AxolotlChannelInitializer
import eu.mshade.enderchest.axolotl.listener.HandshakeAxolotlListener
import eu.mshade.enderchest.axolotl.listener.MessageAxolotlListener
import eu.mshade.enderchest.listener.*
import eu.mshade.enderchest.listener.packet.*
import eu.mshade.enderchest.marshal.common.*
import eu.mshade.enderchest.marshal.item.LoreItemStackMetadataBuffer
import eu.mshade.enderchest.marshal.item.NameItemStackMetadataBuffer
import eu.mshade.enderchest.marshal.metadata.*
import eu.mshade.enderchest.marshal.world.DifficultyBinaryTagMarshal
import eu.mshade.enderchest.marshal.world.DimensionBinaryTagMarshal
import eu.mshade.enderchest.marshal.world.LevelTypeBinaryTagMarshal
import eu.mshade.enderchest.world.ChunkSafeguard
import eu.mshade.enderchest.world.DefaultChunkGenerator
import eu.mshade.enderchest.world.SchematicLoader
import eu.mshade.enderchest.world.WorldManager
import eu.mshade.enderchest.world.virtual.VirtualWorldManager
import eu.mshade.enderframe.EnderFrame
import eu.mshade.enderframe.GameMode
import eu.mshade.enderframe.entity.CreeperState
import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.entity.VillagerType
import eu.mshade.enderframe.event.*
import eu.mshade.enderframe.inventory.InventoryTracker
import eu.mshade.enderframe.item.ItemStackMetadataKey
import eu.mshade.enderframe.metadata.MetadataKeyValueBufferRegistry
import eu.mshade.enderframe.item.Material
import eu.mshade.enderframe.item.MaterialKey.DefaultMaterialKey
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket
import eu.mshade.enderframe.mojang.GameProfile
import eu.mshade.enderframe.mojang.NamespacedKey
import eu.mshade.enderframe.mojang.Property
import eu.mshade.enderframe.mojang.chat.*
import eu.mshade.enderframe.packetevent.*
import eu.mshade.enderframe.protocol.MinecraftEncryption
import eu.mshade.enderframe.protocol.MinecraftProtocolRepository
import eu.mshade.enderframe.tick.TickBus
import eu.mshade.enderframe.world.*
import eu.mshade.enderframe.world.block.BlockMetadataType
import eu.mshade.enderframe.world.chunk.Chunk
import eu.mshade.enderman.EndermanMinecraftProtocol
import eu.mshade.mwork.MWork

import eu.mshade.mwork.binarytag.segment.SegmentBinaryTag
import eu.mshade.stone.StoneAxolotlProtocol

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.function.Consumer

fun main() {
    EnderChest
}

object EnderChest {

    private val LOGGER = LoggerFactory.getLogger(EnderChest::class.java)

    val parentGroup: EventLoopGroup

    private val childGroup: EventLoopGroup
    val players: Queue<Player> = ConcurrentLinkedQueue()
    val minecraftEncryption = MinecraftEncryption()
    val minecraftProtocolRepository = MinecraftProtocolRepository()
    val worldManager: WorldManager
    val virtualWorldManager: VirtualWorldManager
    val metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry
    val chunkSafeguard = ChunkSafeguard()


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
        childGroup = NioEventLoopGroup(Runtime.getRuntime().availableProcessors())
        parentGroup = NioEventLoopGroup(Runtime.getRuntime().availableProcessors())

        //register minecraft protocol 1.8 to 1.19
        minecraftProtocolRepository.register(EndermanMinecraftProtocol())

        val textComponentSerializer = TextComponentSerializer()
        val objectMapper = MWork.get().objectMapper
        val simpleModule = SimpleModule()
        simpleModule.addSerializer(TextComponentEntry::class.java, textComponentSerializer)
        simpleModule.addSerializer(TextComponent::class.java, textComponentSerializer)
        simpleModule.addSerializer(TextClickEvent::class.java, TextClickEventSerializer())
        simpleModule.addSerializer(TextHoverEvent::class.java, TextHoverEventSerializer())
        objectMapper.registerModule(simpleModule)
        val enderFrame = EnderFrame.get()

        val packetEventBus = enderFrame.packetEventBus
        val binaryTagDriver = enderFrame.binaryTagDriver

        packetEventBus.subscribe(MinecraftPacketHandshakeEvent::class.java, MinecraftPacketHandshakeListener(this))
        packetEventBus.subscribe(MinecraftPacketServerPingEvent::class.java,
            MinecraftPacketServerPingListener()
        )
        packetEventBus.subscribe(MinecraftPacketServerStatusEvent::class.java,
            MinecraftPacketServerStatusListener()
        )
        packetEventBus.subscribe(MinecraftPacketLoginEvent::class.java, MinecraftPacketLoginListener(this))
        packetEventBus.subscribe(MinecraftPacketEncryptionEvent::class.java,
            MinecraftPacketEncryptionListener(this)
        )
        packetEventBus.subscribe(MinecraftPacketKeepAliveEvent::class.java,
            MinecraftPacketKeepAliveListener(this)
        )
        packetEventBus.subscribe(MinecraftPacketClientSettingsEvent::class.java,
            MinecraftPacketClientSettingsListener()
        )
        packetEventBus.subscribe(MinecraftPacketChatMessageEvent::class.java, MinecraftPacketChatMessageListener(this))
        packetEventBus.subscribe(MinecraftPacketEntityActionEvent::class.java,
            MinecraftPacketEntityActionListener()
        )
        packetEventBus.subscribe(MinecraftPacketMoveEvent::class.java,
            MinecraftPacketMoveListener()
        )
        packetEventBus.subscribe(MinecraftPacketLookEvent::class.java,
            MinecraftPacketLookListener()
        )
        packetEventBus.subscribe(MinecraftPacketMoveAndLookEvent::class.java,
            MinecraftPacketMoveAndLookListener()
        )
        packetEventBus.subscribe(MinecraftPacketToggleFlyingEvent::class.java, MinecraftPacketToggleFlyingListener())
        packetEventBus.subscribe(MinecraftPacketBlockPlaceEvent::class.java,
            MinecraftPacketBlockPlaceListener()
        )
        packetEventBus.subscribe(MinecraftPacketPlayerDiggingEvent::class.java,
            MinecraftPacketPlayerDiggingListener()
        )
        packetEventBus.subscribe(MinecraftPacketCloseInventoryEvent::class.java,
            MinecraftPacketCloseInventoryListener()
        )
        packetEventBus.subscribe(MinecraftPacketClickInventoryEvent::class.java,
            MinecraftPacketClickInventoryListener()
        )
        packetEventBus.subscribe(MinecraftPacketClientStatusEvent::class.java, MinecraftPacketClientStatusListener())

        val enderFrameEventBus = enderFrame.enderFrameEventBus
        enderFrameEventBus.subscribe(EntityUnseeEvent::class.java,
            EntityUnseeListener()
        )
        enderFrameEventBus.subscribe(EntitySeeEvent::class.java,
            EntitySeeListener()
        )
        enderFrameEventBus.subscribe(ChunkSeeEvent::class.java,
            ChunkSeeListener()
        )
        enderFrameEventBus.subscribe(ChunkUnseeEvent::class.java,
            ChunkUnseeListener()
        )
        enderFrameEventBus.subscribe(EntityMoveEvent::class.java,
            EntityMoveListener()
        )
        enderFrameEventBus.subscribe(EntityTeleportEvent::class.java,
            EntityTeleportListener()
        )
        enderFrameEventBus.subscribe(EntityChunkChangeEvent::class.java,
            EntityChunkChangeListener()
        )
        enderFrameEventBus.subscribe(ChunkUnloadEvent::class.java,
            ChunkUnloadListener()
        )
        enderFrameEventBus.subscribe(ChunkLoadEvent::class.java,
            ChunkLoadListener()
        )
        enderFrameEventBus.subscribe(WatchdogSeeEvent::class.java,
            WatchdogSeeListener()
        )
        enderFrameEventBus.subscribe(ChunkCreateEvent::class.java, ChunkCreateListener())
        enderFrameEventBus.subscribe(WatchdogUnseeEvent::class.java,
            WatchdogUnseeListener()
        )
        enderFrameEventBus.subscribe(
            PlayerDisconnectEvent::class.java,
            PlayerDisconnectListener(this)
        )
        enderFrameEventBus.subscribe(PrePlayerJoinEvent::class.java,
            PrePlayerJoinListener(this)
        )

        metadataKeyValueBufferRegistry = MetadataKeyValueBufferRegistry()
        metadataKeyValueBufferRegistry.register(WorldMetadataType.NAME, NameWorldMetadataBuffer())
        metadataKeyValueBufferRegistry.register(WorldMetadataType.SEED, SeedWorldMetadataBuffer())
        metadataKeyValueBufferRegistry.register(WorldMetadataType.DIMENSION, DimensionWorldMetadataBuffer(binaryTagDriver))
        metadataKeyValueBufferRegistry.register(WorldMetadataType.LEVEL_TYPE, LevelTypeWorldMetadataBuffer(binaryTagDriver))
        metadataKeyValueBufferRegistry.register(WorldMetadataType.DIFFICULTY, DifficultyWorldMetadataBuffer(binaryTagDriver))
        metadataKeyValueBufferRegistry.register(WorldMetadataType.PARENT, ParentWorldMetadataBuffer())

        metadataKeyValueBufferRegistry.register(BlockMetadataType.EXTRA, ExtraBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.FACE, FaceBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.HALF, HalfBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.SHAPE, ShapeBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.AXIS, AxisBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.POWERED, PoweredBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.POWER, PowerBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.DECAYABLE, DecayableBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.CHECK_DECAY, CheckDecayBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.SEAMLESS, SeamlessBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.MULTIPLE_FACE, MultipleFaceBlockMetadataBuffer())

        metadataKeyValueBufferRegistry.register(ItemStackMetadataKey.NAME, NameItemStackMetadataBuffer())
        metadataKeyValueBufferRegistry.register(ItemStackMetadataKey.LORE, LoreItemStackMetadataBuffer())


        binaryTagDriver.registerMarshal(GameMode::class.java, DefaultGameModeMarshal())
        binaryTagDriver.registerMarshal(GameProfile::class.java, DefaultGameProfileMarshal())
        binaryTagDriver.registerMarshal(Property::class.java, DefaultPropertyMarshal())
        binaryTagDriver.registerMarshal(VillagerType::class.java, DefaultVillagerTypeMarshal())
        binaryTagDriver.registerMarshal(Rotation::class.java, DefaultRotationMarshal())
        binaryTagDriver.registerMarshal(Difficulty::class.java, DifficultyBinaryTagMarshal())
        binaryTagDriver.registerMarshal(Dimension::class.java, DimensionBinaryTagMarshal())
        binaryTagDriver.registerMarshal(LevelType::class.java, LevelTypeBinaryTagMarshal())


        chunkSafeguard.start()
        LOGGER.info("ChunkSafeGuard started")

        worldManager = WorldManager(binaryTagDriver, chunkSafeguard, tickBus)
        virtualWorldManager = VirtualWorldManager(chunkSafeguard, tickBus)

        val mapper = ObjectMapper()
        val materialsId = mapper.readTree(this::class.java.getResourceAsStream("/materials.json"))
        materialsId.fields().forEach { (key, value) ->
            val material = Material.fromNamespacedKey(NamespacedKey.minecraft(key))
            if(material == null){
                LOGGER.warn("Material $key not found")
                return@forEach
            }
            (material as DefaultMaterialKey).id = value.asInt()
            Material.registerMaterialKey(material)
        }
        

        val world = worldManager.createWorld("world") { metadataKeyValueBucket ->
            metadataKeyValueBucket.setMetadataKeyValue(SeedWorldMetadata(-4975988339999789512L))
            metadataKeyValueBucket.setMetadataKeyValue(LevelTypeWorldMetadata(LevelType.DEFAULT))
            metadataKeyValueBucket.setMetadataKeyValue(DimensionWorldMetadata(Dimension.OVERWORLD))
            metadataKeyValueBucket.setMetadataKeyValue(DifficultyWorldMetadata(Difficulty.NORMAL))
        }
        world.chunkGenerator = DefaultChunkGenerator(world)

        val threadTickBus = Thread(tickBus, "TickBus")
        threadTickBus.start()

        InventoryTracker.joinTickBus(tickBus)
        LOGGER.info("InventoryTracker joined TickBus")

        LOGGER.info("Starting $threadTickBus")

        /**
         * @TODO later delete this
         */
        SchematicLoader.SCHEMATIC_FOLDER.mkdir()

        Runtime.getRuntime().addShutdownHook(Thread {
            LOGGER.warn("Beginning save of server don't close the console !")
            chunkSafeguard.stopSafeguard()
            WorldRepository.getWorlds().forEach(Consumer { w: World ->
                LOGGER.info("Saving world " + w.name)
                w.saveWorld()
                w.chunks.forEach(Consumer { chunkCompletableFuture: CompletableFuture<Chunk?> ->
                    w.saveChunk(chunkCompletableFuture.join())
                })
                // log number of chunks saved in the world
                LOGGER.info("Saved " + w.chunks.size + " chunks in world " + w.name)
                w.regions.forEach(Consumer { segmentBinaryTag: SegmentBinaryTag -> if (segmentBinaryTag.compoundSectionIndex.consume()) segmentBinaryTag.writeCompoundSectionIndex() })
            })

            virtualWorldManager.getVirtualWorlds().forEach{
                it.saveWorld()
                it.chunks.forEach { chunkCompletableFuture: CompletableFuture<Chunk?> -> it.saveChunk(chunkCompletableFuture.join()) }
                LOGGER.info("Saved " + it.chunks.size + " chunks in virtual world " + it.name)
                it.regions.forEach { segmentBinaryTag: SegmentBinaryTag -> if (segmentBinaryTag.compoundSectionIndex.consume()) segmentBinaryTag.writeCompoundSectionIndex() }
            }

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

        val axolotlPacketInEventBus = Axolotl.eventBus
        axolotlPacketInEventBus.subscribe(HandshakeAxolotlEvent::class.java, HandshakeAxolotlListener())
        axolotlPacketInEventBus.subscribe(ChatMessageAxolotlEvent::class.java, MessageAxolotlListener())
/*        axolotlPacketInEventBus.subscribe(AxolotlEvent::class.java, object : EventListener<AxolotlEvent> {
            override fun onEvent(event: AxolotlEvent) {
                LOGGER.info("Received event $event")
            }
        }, eventFilter = EventFilter.DERIVE)*/

        val axolotlProtocolRepository = AxolotlProtocolRepository
        axolotlProtocolRepository.register(StoneAxolotlProtocol())


        val axolotlServer = ServerBootstrap()
            .group(NioEventLoopGroup(), NioEventLoopGroup())
            .channel(NioServerSocketChannel::class.java)
            .childHandler(AxolotlChannelInitializer())
            .localAddress("0.0.0.0", 25656)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .bind()

        try {
            LOGGER.info(axolotlServer.sync().channel().toString())
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
/*        parentGroup.scheduleAtFixedRate({

            worldManager.worlds.forEach(Consumer { w: World ->
                LOGGER.info("World " + w.name + " has " + w.chunks.size + " chunks")
            })
        }, 0, 1, TimeUnit.SECONDS)*/

    }


    fun addPlayer(player: Player) {
        players.add(player)
    }
    fun removePlayer(player: Player) {
        players.remove(player)
    }
}
