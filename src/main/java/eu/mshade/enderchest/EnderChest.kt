package eu.mshade.enderchest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import eu.mshade.enderchest.listener.WatchdogSeeListener
import eu.mshade.enderchest.listener.WatchdogUnseeListener
import eu.mshade.enderchest.listener.animation.SwingArmListener
import eu.mshade.enderchest.listener.chunk.*
import eu.mshade.enderchest.listener.entity.*
import eu.mshade.enderchest.listener.packet.*
import eu.mshade.enderchest.listener.player.LoginSuccessListener
import eu.mshade.enderchest.listener.player.PlayerDisconnectListener
import eu.mshade.enderchest.listener.player.PlayerJoinListener
import eu.mshade.enderchest.listener.player.PrePlayerJoinListener
import eu.mshade.enderchest.marshal.item.LoreItemStackMetadataBuffer
import eu.mshade.enderchest.marshal.item.NameItemStackMetadataBuffer
import eu.mshade.enderchest.marshal.metadata.*
import eu.mshade.enderchest.plugin.DefaultPluginManager
import eu.mshade.enderchest.world.ChunkSafeguard
import eu.mshade.enderchest.world.SchematicLoader
import eu.mshade.enderchest.world.WorldManager
import eu.mshade.enderchest.world.generation.TestWorldGeneration
import eu.mshade.enderchest.world.virtual.VirtualWorldManager
import eu.mshade.enderframe.EnderFrame
import eu.mshade.enderframe.MinecraftServer
import eu.mshade.enderframe.entity.EntityTracker
import eu.mshade.enderframe.event.*
import eu.mshade.enderframe.event.animation.SwingArmEvent
import eu.mshade.enderframe.inventory.InventoryTracker
import eu.mshade.enderframe.item.ItemStackMetadataKey
import eu.mshade.enderframe.item.Material
import eu.mshade.enderframe.item.MaterialKey.DefaultMaterialKey
import eu.mshade.enderframe.metadata.MetadataKeyValueBufferRegistry
import eu.mshade.enderframe.mojang.chat.*
import eu.mshade.enderframe.packetevent.*
import eu.mshade.enderframe.plugin.PluginManager
import eu.mshade.enderframe.protocol.MinecraftEncryption
import eu.mshade.enderframe.scoreboard.ScoreboardSidebar
import eu.mshade.enderframe.tick.TickBus
import eu.mshade.enderframe.world.*
import eu.mshade.enderframe.world.block.BlockMetadataType
import eu.mshade.enderman.EndermanMinecraftProtocol
import eu.mshade.mwork.MWork
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.fusesource.jansi.AnsiConsole
import org.slf4j.LoggerFactory
import java.io.File
import java.util.function.Consumer

fun main() {
    EnderChest
}

object EnderChest {

    private val LOGGER = LoggerFactory.getLogger(EnderChest::class.java)

    val parentGroup: EventLoopGroup

    private val childGroup: EventLoopGroup
    val minecraftEncryption = MinecraftEncryption()
    val worldManager: WorldManager
    val virtualWorldManager: VirtualWorldManager
    val metadataKeyValueBufferRegistry: MetadataKeyValueBufferRegistry
    val chunkSafeguard = ChunkSafeguard()

    val metrics = ScoreboardSidebar("EnderChest - Metrics")


    val tickBus = TickBus(20)

    init {
        AnsiConsole.systemUninstall()
        val start = System.currentTimeMillis()
        println(
            "\n" +
                    "███████╗███╗░░██╗██████╗░███████╗██████╗░░█████╗░██╗░░██╗███████╗░██████╗████████╗\n" +
                    "██╔════╝████╗░██║██╔══██╗██╔════╝██╔══██╗██╔══██╗██║░░██║██╔════╝██╔════╝╚══██╔══╝\n" +
                    "█████╗░░██╔██╗██║██║░░██║█████╗░░██████╔╝██║░░╚═╝███████║█████╗░░╚█████╗░░░░██║░░░\n" +
                    "██╔══╝░░██║╚████║██║░░██║██╔══╝░░██╔══██╗██║░░██╗██╔══██║██╔══╝░░░╚═══██╗░░░██║░░░\n" +
                    "███████╗██║░╚███║██████╔╝███████╗██║░░██║╚█████╔╝██║░░██║███████╗██████╔╝░░░██║░░░\n" +
                    "╚══════╝╚═╝░░╚══╝╚═════╝░╚══════╝╚═╝░░╚═╝░╚════╝░╚═╝░░╚═╝╚══════╝╚═════╝░░░░╚═╝░░░"
        )
        LOGGER.info("Starting EnderChest")
        childGroup = NioEventLoopGroup(Runtime.getRuntime().availableProcessors())
        parentGroup = NioEventLoopGroup(Runtime.getRuntime().availableProcessors())

        val mapper = ObjectMapper()
        val materialsId = mapper.readTree(this::class.java.getResourceAsStream("/materials.json"))
        LOGGER.info("Loading ids from materials.json")
        Material.getRegisteredNamespacedKeys().forEach { key ->
            val id = materialsId[key.key]
            if (id == null) {
                LOGGER.warn("Material $key not found")
                return@forEach
            }
            val material = Material.fromName(key)
            (material as DefaultMaterialKey).id = id.asInt()
            Material.registerMaterialKey(material)
        }
        LOGGER.info("Loaded ids from materials.json")

        val minecraftProtocols = MinecraftServer.getMinecraftProtocols()
        val worldRepository = MinecraftServer.getWorldRepository()
        val tickableBlocks = MinecraftServer.getTickableBlocks()
        //register minecraft protocol 1.8 to 1.19
        minecraftProtocols.register(EndermanMinecraftProtocol())

        val textComponentSerializer = TextComponentSerializer()
        val objectMapper = MWork.getObjectMapper()
        val simpleModule = SimpleModule()
        simpleModule.addSerializer(TextComponentEntry::class.java, textComponentSerializer)
        simpleModule.addSerializer(TextComponent::class.java, textComponentSerializer)
        simpleModule.addSerializer(TextClickEvent::class.java, TextClickEventSerializer())
        simpleModule.addSerializer(TextHoverEvent::class.java, TextHoverEventSerializer())
        objectMapper.registerModule(simpleModule)
        val enderFrame = EnderFrame.get()

        val packetEvents = enderFrame.packetEvents
        val binaryTagDriver = enderFrame.binaryTagDriver

        packetEvents.subscribe(MinecraftPacketHandshakeEvent::class.java, MinecraftPacketHandshakeListener())
        packetEvents.subscribe(MinecraftPacketServerPingEvent::class.java, MinecraftPacketServerPingListener())
        packetEvents.subscribe(MinecraftPacketServerStatusEvent::class.java, MinecraftPacketServerStatusListener())
        packetEvents.subscribe(MinecraftPacketLoginEvent::class.java, MinecraftPacketLoginListener())
        packetEvents.subscribe(MinecraftPacketEncryptionEvent::class.java, MinecraftPacketEncryptionListener())
        packetEvents.subscribe(MinecraftPacketKeepAliveEvent::class.java, MinecraftPacketKeepAliveListener())
        packetEvents.subscribe(MinecraftPacketClientSettingsEvent::class.java, MinecraftPacketClientSettingsListener())
        packetEvents.subscribe(MinecraftPacketChatMessageEvent::class.java, MinecraftPacketChatMessageListener())
        packetEvents.subscribe(MinecraftPacketEntityActionEvent::class.java, MinecraftPacketEntityActionListener())
        packetEvents.subscribe(MinecraftPacketMoveEvent::class.java, MinecraftPacketMoveListener())
        packetEvents.subscribe(MinecraftPacketLookEvent::class.java, MinecraftPacketLookListener())
        packetEvents.subscribe(MinecraftPacketMoveAndLookEvent::class.java, MinecraftPacketMoveAndLookListener())
        packetEvents.subscribe(MinecraftPacketToggleFlyingEvent::class.java, MinecraftPacketToggleFlyingListener())
        packetEvents.subscribe(MinecraftPacketBlockPlaceEvent::class.java, MinecraftPacketBlockPlaceListener())
        packetEvents.subscribe(MinecraftPacketPlayerDiggingEvent::class.java, MinecraftPacketPlayerDiggingListener())
        packetEvents.subscribe(MinecraftPacketCloseInventoryEvent::class.java, MinecraftPacketCloseInventoryListener())
        packetEvents.subscribe(MinecraftPacketClickInventoryEvent::class.java, MinecraftPacketClickInventoryListener())
        packetEvents.subscribe(MinecraftPacketClientStatusEvent::class.java, MinecraftPacketClientStatusListener())
        packetEvents.subscribe(MinecraftPacketHeldItemChangeEvent::class.java, MinecraftPacketHeldItemChangeListener())
        packetEvents.subscribe(MinecraftPacketAnimationEvent::class.java, MinecraftPacketAnimationListener())


        val minecraftEvents = MinecraftServer.getMinecraftEvent()
        minecraftEvents.subscribe(EntityUnseeEvent::class.java, EntityUnseeListener())
        minecraftEvents.subscribe(EntitySeeEvent::class.java, EntitySeeListener())
        minecraftEvents.subscribe(ChunkSeeEvent::class.java, ChunkSeeListener())
        minecraftEvents.subscribe(ChunkUnseeEvent::class.java, ChunkUnseeListener())
        minecraftEvents.subscribe(EntityMoveEvent::class.java, EntityMoveListener())
        minecraftEvents.subscribe(EntityTeleportEvent::class.java, EntityTeleportListener())
        minecraftEvents.subscribe(EntityChunkChangeEvent::class.java, EntityChunkChangeListener())
        minecraftEvents.subscribe(ChunkUnloadEvent::class.java, ChunkUnloadListener())
        minecraftEvents.subscribe(ChunkLoadEvent::class.java, ChunkLoadListener())
        minecraftEvents.subscribe(WatchdogSeeEvent::class.java, WatchdogSeeListener())
        minecraftEvents.subscribe(ChunkCreateEvent::class.java, ChunkCreateListener())
        minecraftEvents.subscribe(WatchdogUnseeEvent::class.java, WatchdogUnseeListener())
        minecraftEvents.subscribe(PlayerDisconnectEvent::class.java, PlayerDisconnectListener())
        minecraftEvents.subscribe(PrePlayerJoinEvent::class.java, PrePlayerJoinListener())
        minecraftEvents.subscribe(PlayerJoinEvent::class.java, PlayerJoinListener())
        minecraftEvents.subscribe(SwingArmEvent::class.java, SwingArmListener())
        minecraftEvents.subscribe(LoginSuccessEvent::class.java, LoginSuccessListener())

        metadataKeyValueBufferRegistry = MetadataKeyValueBufferRegistry()
        metadataKeyValueBufferRegistry.register(WorldMetadataType.NAME, NameWorldMetadataBuffer())
        metadataKeyValueBufferRegistry.register(WorldMetadataType.SEED, SeedWorldMetadataBuffer())
        metadataKeyValueBufferRegistry.register(WorldMetadataType.DIMENSION, DimensionWorldMetadataBuffer(binaryTagDriver))
        metadataKeyValueBufferRegistry.register(WorldMetadataType.LEVEL_TYPE, LevelTypeWorldMetadataBuffer(binaryTagDriver))
        metadataKeyValueBufferRegistry.register(WorldMetadataType.DIFFICULTY, DifficultyWorldMetadataBuffer(binaryTagDriver))
        metadataKeyValueBufferRegistry.register(WorldMetadataType.PARENT, ParentWorldMetadataBuffer(worldRepository))

        metadataKeyValueBufferRegistry.register(BlockMetadataType.EXTRA, ExtraBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.FACE, FaceBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.HALF, HalfBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.SHAPE, ShapeBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.AXIS, AxisBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.POWER, PowerBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.DECAYABLE, DecayableBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.CHECK_DECAY, CheckDecayBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.SEAMLESS, SeamlessBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.MULTIPLE_FACE, MultipleFaceBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.SLAB_TYPE, SlabTypeBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.INVENTORY, InventoryBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.TICKABLE, TickableBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.TICK, TickBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.DELAY, DelayBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.REDSTONE_STATE, RedstoneStateBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.LOCKED, LockedBlockMetadataBuffer())
        metadataKeyValueBufferRegistry.register(BlockMetadataType.POWER_SOURCE, PowerSourceBlockMetadataBuffer())


        metadataKeyValueBufferRegistry.register(ItemStackMetadataKey.NAME, NameItemStackMetadataBuffer())
        metadataKeyValueBufferRegistry.register(ItemStackMetadataKey.LORE, LoreItemStackMetadataBuffer())

        val pluginManager: PluginManager = DefaultPluginManager(mapper)
        pluginManager.loadPlugins(File(System.getProperty("user.dir"), "plugins").toPath())
        pluginManager.enablePlugins()

        chunkSafeguard.start()
        LOGGER.info("ChunkSafeGuard started")


        val threadTickBus = Thread(tickBus, "TickBus")
        threadTickBus.start()

        InventoryTracker.joinTickBus(tickBus)
        LOGGER.info("InventoryTracker joined TickBus")

        EntityTracker.joinTickBus(tickBus)
        LOGGER.info("EntityTracker joined TickBus")

        LOGGER.info("Starting $threadTickBus")

        /**
         * @TODO later delete this
         */
        SchematicLoader.SCHEMATIC_FOLDER.mkdir()

        Metrics().joinTickBus(tickBus)

        tickableBlocks.joinTickBus(tickBus)

        worldManager = WorldManager(binaryTagDriver, chunkSafeguard, tickBus)
        virtualWorldManager = VirtualWorldManager(chunkSafeguard, tickBus)


        val world = worldManager.createWorld("world") { metadataKeyValueBucket ->
            metadataKeyValueBucket.setMetadataKeyValue(SeedWorldMetadata(-4975988339999789512L))
            metadataKeyValueBucket.setMetadataKeyValue(LevelTypeWorldMetadata(LevelType.DEFAULT))
            metadataKeyValueBucket.setMetadataKeyValue(DimensionWorldMetadata(Dimension.OVERWORLD))
            metadataKeyValueBucket.setMetadataKeyValue(DifficultyWorldMetadata(Difficulty.NORMAL))
        }
//        world.chunkGenerator = DefaultChunkGenerator(world)
        world.chunkGenerator = TestWorldGeneration()



        Runtime.getRuntime().addShutdownHook(Thread {
            LOGGER.warn("Beginning save of server don't close the console !")
            chunkSafeguard.stopSafeguard()
            worldRepository.getWorlds().forEach(Consumer { w: World ->
                LOGGER.info("Saving world " + w.name)
                w.saveLevel()
                w.chunks.forEach{chunk ->
                    tickableBlocks.flush(chunk.join())
                    w.saveChunk(chunk.join())
                }
                // log number of chunks saved in the world
                LOGGER.info("Saved " + w.chunks.size + " chunks in world " + w.name)
            })

            virtualWorldManager.getVirtualWorlds().forEach {
                it.saveLevel()
                it.chunks.forEach { chunk ->
                    tickableBlocks.flush(chunk.join())
                    it.saveChunk(chunk.join())
                }
                LOGGER.info("Saved " + it.chunks.size + " chunks in virtual world " + it.name)
            }

            LOGGER.info("Worlds saved")
            parentGroup.shutdownGracefully()
            childGroup.shutdownGracefully()
        })

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

    }

}
