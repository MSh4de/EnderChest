package eu.mshade.enderchest;

import eu.mshade.enderchest.protocol.ProtocolRepository;
import eu.mshade.enderchest.world.DefaultChunkGenerator;
import eu.mshade.enderchest.world.WorldBufferIO;
import eu.mshade.enderchest.world.WorldManager;
import eu.mshade.enderframe.EnderFrameSession;
import eu.mshade.enderframe.protocol.MinecraftEncryption;
import eu.mshade.enderframe.world.*;
import eu.mshade.enderman.EndermanProtocol;
import io.netty.channel.EventLoopGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DedicatedEnderChest {

    private final Queue<EnderFrameSession> enderFrameSessions = new ConcurrentLinkedQueue<>();
    private final MinecraftEncryption minecraftEncryption = new MinecraftEncryption();
    private final ProtocolRepository protocolRepository = new ProtocolRepository();
    private final WorldManager worldManager;
    private final EventLoopGroup eventLoopGroup;

    public DedicatedEnderChest(EventLoopGroup eventLoopGroup) {
        this.eventLoopGroup = eventLoopGroup;
        this.worldManager = new WorldManager(this);
        this.protocolRepository.register(new EndermanProtocol());

        WorldLevel worldLevel = new WorldLevel("world", -4975988339999789512L, LevelType.DEFAULT, Dimension.OVERWORLD, Difficulty.NORMAL);

        WorldBuffer world = this.worldManager.createWorld(worldLevel);
        world.setChunkGenerator(new DefaultChunkGenerator(world));

    }

    public void addPlayer(EnderFrameSession enderFrameSession){
        this.enderFrameSessions.add(enderFrameSession);
    }

    public void removePlayer(EnderFrameSession enderFrameSession){
        this.enderFrameSessions.remove(enderFrameSession);
    }

    public Queue<EnderFrameSession> getEnderFrameSessions() {
        return enderFrameSessions;
    }

    public ProtocolRepository getProtocolRepository() {
        return protocolRepository;
    }

    public MinecraftEncryption getMinecraftEncryption() {
        return minecraftEncryption;
    }

    public EventLoopGroup getEventLoopGroup() {
        return eventLoopGroup;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

}
