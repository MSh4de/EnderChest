package eu.mshade.enderchest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.mshade.enderchest.protocol.listener.*;
import eu.mshade.enderchest.redstone.Redstone;
import eu.mshade.enderchest.redstone.protocol.RedstonePacketIn;
import eu.mshade.enderchest.redstone.protocol.RedstonePacketInDeserializer;
import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.event.PacketEvent;
import eu.mshade.enderframe.event.entity.*;
import eu.mshade.enderframe.event.server.ServerPingEvent;
import eu.mshade.enderframe.event.server.ServerStatusEvent;
import eu.mshade.enderframe.mojang.chat.*;
import eu.mshade.mwork.MWork;
import eu.mshade.mwork.event.EventBus;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class EnderChest {

    private final EventLoopGroup eventLoopGroup;
    private Redstone redstone;
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

    public EventLoopGroup getEventLoopGroup() {
        return eventLoopGroup;
    }

    public DedicatedEnderChest getDedicatedEnderChest() {
        return dedicatedEnderChest;
    }
}
