package eu.mshade.enderchest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.mshade.enderchest.entity.*;
import eu.mshade.enderchest.marshals.assets.DefaultAgeableMarshal;
import eu.mshade.enderchest.marshals.assets.DefaultRideableMarshal;
import eu.mshade.enderchest.marshals.entity.*;
import eu.mshade.enderchest.marshals.utils.DefaultRotationMarshal;
import eu.mshade.enderchest.marshals.utils.DefaultVectorMarshal;
import eu.mshade.enderchest.marshals.world.DefaultChunkMarshal;
import eu.mshade.enderchest.marshals.world.DefaultLocationMarshal;
import eu.mshade.enderchest.marshals.world.DefaultSectionMarshal;
import eu.mshade.enderchest.protocol.listener.*;
import eu.mshade.enderchest.world.DefaultChunkBuffer;
import eu.mshade.enderchest.world.DefaultSectionBuffer;
import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.entity.*;
import eu.mshade.enderframe.event.PacketEvent;
import eu.mshade.enderframe.event.entity.*;
import eu.mshade.enderframe.event.server.ServerPingEvent;
import eu.mshade.enderframe.event.server.ServerStatusEvent;
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
        packetEventBus.subscribe(PacketMoveEvent.class, new PacketMoveHandler(dedicatedEnderChest));
        packetEventBus.subscribe(PacketQuitEvent.class, new PacketQuitHandler(dedicatedEnderChest));
        packetEventBus.subscribe(PacketEntityActionEvent.class, new PacketEntityActionHandler());

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

        eventLoopGroup.scheduleAtFixedRate(() -> {
            dedicatedEnderChest.getEnderFrameSessions().forEach(enderFrameSession -> {
                enderFrameSession.sendKeepAlive((int) System.currentTimeMillis());
            });
        }, 0, 1, TimeUnit.SECONDS);

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

}
