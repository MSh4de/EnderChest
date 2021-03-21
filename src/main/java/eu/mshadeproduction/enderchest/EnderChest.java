package eu.mshadeproduction.enderchest;

import eu.mshadeproduction.enderchest.listener.HandshakeListener;
import eu.mshadeproduction.enderchest.listener.ServerPingListener;
import eu.mshadeproduction.enderchest.listener.ServerStatusListener;
import eu.mshadeproduction.enderframe.EnderFrame;
import eu.mshadeproduction.enderframe.event.Event;
import eu.mshadeproduction.enderframe.event.player.HandshakeEvent;
import eu.mshadeproduction.enderframe.event.server.ServerPingEvent;
import eu.mshadeproduction.enderframe.event.server.ServerStatusEvent;
import eu.mshadeproduction.mwork.dispatcher.DispatcherDriver;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EnderChest {

    private final EventLoopGroup eventExecutors = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

    public EnderChest() {
        EnderFrame enderFrame = EnderFrame.get();
        DispatcherDriver<Event> eventDispatcherDriver = enderFrame.getEventDispatcherDriver();

        eventDispatcherDriver.register(HandshakeEvent.class, new HandshakeListener());
        eventDispatcherDriver.register(ServerPingEvent.class, new ServerPingListener());
        eventDispatcherDriver.register(ServerStatusEvent.class, new ServerStatusListener());


        new ServerBootstrap()
                .group(eventExecutors)
                .childHandler(new EnderChestChannelInitializer())
                .channel(NioServerSocketChannel.class)
                .localAddress("0.0.0.0", 25565)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .bind();

    }

    public static void main(String[] args) {
        new EnderChest();
    }

}
