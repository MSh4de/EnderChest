package eu.mshade.enderchest;

import eu.mshade.enderchest.protocol.ProtocolRepository;
import eu.mshade.enderchest.world.DefaultChunkGenerator;
import eu.mshade.enderchest.world.WorldManager;
import eu.mshade.enderframe.EnderFrameSession;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.protocol.MinecraftEncryption;
import eu.mshade.enderframe.world.*;
import eu.mshade.enderman.EndermanProtocol;
import io.netty.channel.EventLoopGroup;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DedicatedEnderChest {

    private final Queue<Player> players = new ConcurrentLinkedQueue<>();
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

        //eventLoopGroup.scheduleAtFixedRate(chunkBucket, 0, 100000, TimeUnit.NANOSECONDS);

    }

    public void addPlayer(Player player){
        this.players.add(player);
    }

    public void removePlayer(Player player){
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

    public EventLoopGroup getEventLoopGroup() {
        return eventLoopGroup;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DedicatedEnderChest that = (DedicatedEnderChest) o;
        return Objects.equals(players, that.players) && Objects.equals(minecraftEncryption, that.minecraftEncryption) && Objects.equals(protocolRepository, that.protocolRepository) && Objects.equals(worldManager, that.worldManager) && Objects.equals(eventLoopGroup, that.eventLoopGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(players, minecraftEncryption, protocolRepository, worldManager, eventLoopGroup);
    }
}
