package eu.mshade.enderchest.world;

import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.item.MaterialKey;
import eu.mshade.enderframe.world.Chunk;
import eu.mshade.enderframe.world.ChunkStatus;
import eu.mshade.enderframe.world.Section;
import eu.mshade.enderframe.world.World;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class EmptyChunk implements Chunk {

    private final int x;
    private final int z;
    private final UUID id;
    private final World world;
    private final Section[] sections = new Section[16];
    private final Queue<Entity> entities = new ConcurrentLinkedQueue<>();
    private final Queue<Player> players = new ConcurrentLinkedQueue<>();
    private ChunkStatus chunkStatus = ChunkStatus.LOADED;

    public EmptyChunk(int x, int z, UUID id, World world) {
        this.x = x;
        this.z = z;
        this.id = id;
        this.world = world;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public Queue<Player> getViewers() {
        return players;
    }

    @Override
    public void addViewer(Player player) {
        this.players.add(player);
    }

    @Override
    public void removeViewer(Player player) {
        this.players.remove(player);
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public Section[] getSections() {
        return sections;
    }

    @Override
    public AtomicLong getLastInteract() {
        return null;
    }

    @Override
    public ChunkStatus getChunkStatus() {
        return chunkStatus;
    }

    @Override
    public void setChunkStatus(ChunkStatus chunkStatus) {
        this.chunkStatus = chunkStatus;
    }

    @Override
    public int getBitMask() {
        return 0;
    }

    @Override
    public int getBlock(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlock(int x, int y, int z, MaterialKey block) {

    }

    @Override
    public byte getBlockLight(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlockLight(int x, int y, int z, byte light) {

    }

    @Override
    public byte getSkyLight(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setSkyLight(int x, int y, int z, byte light) {

    }

    @Override
    public void setBiome(int x, int z, int biome) {

    }

    @Override
    public int getBiome(int x, int z) {
        return 0;
    }

    @Override
    public byte[] getBiomes() {
        return new byte[0];
    }

    @Override
    public int getHighest(int x, int z) {
        return 0;
    }

    @Override
    public boolean hasChange() {
        return false;
    }

    @Override
    public Queue<Entity> getEntities() {
        return entities;
    }

    @Override
    public void addEntity(Entity entity) {

    }

    @Override
    public void removeEntity(Entity entity) {

    }

    @Override
    public void clearEntities() {

    }
}
