package eu.mshade.enderchest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.mshade.enderchest.protocol.listener.*;
import eu.mshade.enderchest.world.WorldBufferIO;
import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.event.PacketEvent;
import eu.mshade.enderframe.event.entity.*;
import eu.mshade.enderframe.event.server.ServerPingEvent;
import eu.mshade.enderframe.event.server.ServerStatusEvent;
import eu.mshade.enderframe.mojang.chat.*;
import eu.mshade.mwork.MWork;
import eu.mshade.mwork.dispatcher.DispatcherDriver;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
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
        DispatcherDriver<PacketEvent> eventDispatcherDriver = enderFrame.getPacketEventDriver();

        eventDispatcherDriver.register(PacketHandshakeEvent.class, new PacketHandshakeListener(dedicatedEnderChest));
        eventDispatcherDriver.register(ServerPingEvent.class, new ServerPingListener());
        eventDispatcherDriver.register(ServerStatusEvent.class, new ServerStatusListener());
        eventDispatcherDriver.register(PacketLoginEvent.class, new PacketLoginHandler(dedicatedEnderChest));
        eventDispatcherDriver.register(PacketEncryptionEvent.class, new PacketEncryptionHandler(dedicatedEnderChest));
        eventDispatcherDriver.register(PacketKeepAliveEvent.class, new PacketKeepAliveHandler(dedicatedEnderChest));
        eventDispatcherDriver.register(PacketClientSettingsEvent.class, new PacketClientSettingsHandler());
        eventDispatcherDriver.register(PacketChatMessageEvent.class, new PacketChatMessageHandler(dedicatedEnderChest));
        eventDispatcherDriver.register(PacketFinallyJoinEvent.class, new PacketFinallyJoinHandler(dedicatedEnderChest));
        eventDispatcherDriver.register(PacketMoveEvent.class, new PacketMoveHandler(dedicatedEnderChest));
        eventDispatcherDriver.register(PacketQuitEvent.class, new PacketQuitHandler(dedicatedEnderChest));

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
                .childHandler(new EnderChestChannelInitializer())
                .channel(NioServerSocketChannel.class)
                .localAddress("0.0.0.0", 25565)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .bind();
        logger.info("Done !");
    }

    public static void main(String[] args) {
        new EnderChest();
    }

}
