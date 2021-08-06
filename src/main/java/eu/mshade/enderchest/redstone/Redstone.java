package eu.mshade.enderchest.redstone;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderchest.EnderChestChannelInitializer;
import eu.mshade.enderchest.redstone.protocol.RedstonePacketIn;
import eu.mshade.enderchest.redstone.protocol.RedstonePacketOut;
import eu.mshade.mwork.event.EventBus;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Redstone {

    private Map<Integer, CompletableFuture<RedstonePacketIn>> integerCompletableFuture = new ConcurrentHashMap<>();
    private Queue<RedstoneSession> redstoneSessions = new ConcurrentLinkedQueue<>();
    private EventBus<RedstonePacketIn> redstonePacketOut = new EventBus<>();
    private Logger logger = LoggerFactory.getLogger(Redstone.class);

    public Redstone(EnderChest enderChest) {
        new ServerBootstrap()
                .group(enderChest.getEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new RedstoneChannelInitializer(this))
                .localAddress("0.0.0.0", 25566)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .bind();
        logger.info("Running redstone service");
    }

    public <T extends RedstonePacketIn> CompletableFuture<T> sendPacket(RedstoneSession redstoneSession, RedstonePacketOut redstonePacketOut, Class<T> redstonePacketIn){
        CompletableFuture<RedstonePacketIn> completableFuture = new CompletableFuture<>();
        integerCompletableFuture.put(redstonePacketOut.getId(), completableFuture);
        redstoneSession.sendPacket(redstonePacketOut);
        return (CompletableFuture<T>) completableFuture;
    }

    public CompletableFuture<RedstonePacketIn> getAwaitRedstonePacketIn(int id){
        return integerCompletableFuture.get(id);
    }

    public void addRedstoneSession(RedstoneSession redstoneSession){
        logger.info("new redstoneSession");
        redstoneSessions.add(redstoneSession);
    }

    public void removeRedstoneSession(RedstoneSession redstoneSession){
        redstoneSessions.remove(redstoneSession);
    }

    public Queue<RedstoneSession> getRedstoneSessions() {
        return redstoneSessions;
    }

    public EventBus<RedstonePacketIn> getRedstonePacketEventBus() {
        return redstonePacketOut;
    }
}
