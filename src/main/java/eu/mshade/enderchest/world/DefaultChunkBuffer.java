package eu.mshade.enderchest.world;

import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.world.ChunkBuffer;
import eu.mshade.enderframe.world.SectionBuffer;
import eu.mshade.enderframe.world.WorldBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public  class DefaultChunkBuffer implements ChunkBuffer {

    private static Logger logger = LoggerFactory.getLogger(DefaultChunkBuffer.class);

    public static final int WIDTH = 16, HEIGHT = 16, DEPTH = 256, SEC_DEPTH = 16;

    private int x;
    private int z;
    private UUID id;
    private File file;
    private boolean hasChange;
    private AtomicLong health = new AtomicLong(System.currentTimeMillis());
    private Queue<Player> players = new ConcurrentLinkedQueue<>();
    private WorldBuffer worldBuffer;
    private SectionBuffer[] sectionBuffers = new SectionBuffer[16];
    private final Queue<Entity> entities = new ConcurrentLinkedQueue<>();
    //private final AtomicReference<ChunkBufferStatus> chunkBufferStatus
    private byte[] biomes;

    public DefaultChunkBuffer(int x, int z, File file, boolean hasChange, WorldBuffer worldBuffer, byte[] biomes) {
        this.x = x;
        this.z = z;
        this.file = file;
        this.hasChange = hasChange;
        this.worldBuffer = worldBuffer;
        this.biomes = biomes;
        this.id = ChunkBuffer.ofId(x, z);
    }

    public DefaultChunkBuffer(int x, int z, boolean hasChange, WorldBuffer worldBuffer, File file) {
        this(x, z, file, hasChange, worldBuffer, new byte[256]);
    }

    @Override
    public int getX() {
        getHealth().set(System.currentTimeMillis());
        return x;
    }

    @Override
    public int getZ() {
        getHealth().set(System.currentTimeMillis());
        return z;
    }

    @Override
    public UUID getId() {
        getHealth().set(System.currentTimeMillis());
        return id;
    }

    @Override
    public Queue<Player> getViewers() {
        getHealth().set(System.currentTimeMillis());
        return players;
    }

    @Override
    public WorldBuffer getWorldBuffer() {
        getHealth().set(System.currentTimeMillis());
        return worldBuffer;
    }

    @Override
    public SectionBuffer[] getSectionBuffers() {
        getHealth().set(System.currentTimeMillis());
        return sectionBuffers;
    }

    @Override
    public AtomicLong getHealth() {
        return health;
    }

    @Override
    public File getFile() {
        getHealth().set(System.currentTimeMillis());
        return file;
    }

    @Override
    public int getBitMask() {
        getHealth().set(System.currentTimeMillis());
        int sectionBitmask = 0;
        for (int i = 0; i < sectionBuffers.length; i++) {
            if (sectionBuffers[i] != null && sectionBuffers[i].getRealBlock() != 0) {
                sectionBitmask |= 1 << i;
            }
        }
        return sectionBitmask;
    }

    @Override
    public int getBlock(int x, int y, int z) {
        getHealth().set(System.currentTimeMillis());
        SectionBuffer sectionBuffer = getSectionBuffer(y);
        return sectionBuffer.getBlocks()[getIndex(x, y, z)];
    }

    @Override
    public void setBlock(int x, int y, int z, int block) {
        getHealth().set(System.currentTimeMillis());
        SectionBuffer sectionBuffer = getSectionBuffer(y);
        if (block == 0){
            int i = sectionBuffer.getRealBlock() - 1;
            sectionBuffer.setRealBlock(Math.max(i, 0));
        }else sectionBuffer.setRealBlock(sectionBuffer.getRealBlock() + 1);
        hasChange = true;
        sectionBuffer.getBlocks()[getIndex(x, y, z)] = block;
    }

    @Override
    public byte getBlockLight(int x, int y, int z) {
        getHealth().set(System.currentTimeMillis());
        SectionBuffer sectionBuffer = getSectionBuffer(y);
        return sectionBuffer.getBlockLight().get(getIndex(x, y, z));
    }

    @Override
    public void setBlockLight(int x, int y, int z, byte light) {
        getHealth().set(System.currentTimeMillis());
        SectionBuffer sectionBuffer = getSectionBuffer(y);
        sectionBuffer.getBlockLight().set(getIndex(x, y, z), light);
        hasChange = true;
    }

    @Override
    public byte getSkyLight(int x, int y, int z) {
        getHealth().set(System.currentTimeMillis());
        SectionBuffer sectionBuffer = getSectionBuffer(y);
        return sectionBuffer.getSkyLight().get(getIndex(x, y, z));
    }

    @Override
    public void setSkyLight(int x, int y, int z, byte light) {
        getHealth().set(System.currentTimeMillis());
        SectionBuffer sectionBuffer = getSectionBuffer(y);
        sectionBuffer.getSkyLight().set(getIndex(x, y, z), light);
        hasChange = true;
    }

    @Override
    public void setBiome(int x, int z, int biome) {
        getHealth().set(System.currentTimeMillis());
        hasChange = true;
        biomes[(z & 0xF) * WIDTH + (x & 0xF)] = (byte) biome;
    }

    @Override
    public int getBiome(int x, int z) {
        getHealth().set(System.currentTimeMillis());
        return biomes[(z & 0xF) * WIDTH + (x & 0xF)] & 0xFF;
    }

    @Override
    public byte[] getBiomes() {
        getHealth().set(System.currentTimeMillis());
        return biomes;
    }

    @Override
    public int getHighest(int x, int z) {
        for (int i = 255; i > 0; i--) {
            if (sectionBuffers[i >> 4] != null && getBlock(x, i, z) != 0) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public boolean hasChange() {
        return hasChange;
    }

    @Override
    public Queue<Entity> getEntities() {
        return this.entities;
    }

    @Override
    public void addEntity(Entity entity) {
        getWorldBuffer().addEntity(entity);
        getEntities().add(entity);
        hasChange = true;
    }

    @Override
    public void removeEntity(Entity entity) {
        getWorldBuffer().removeEntity(entity);
        getEntities().remove(entity);
        hasChange = true;
    }

    public void setBiomes(byte[] biomes) {
        this.biomes = biomes;
    }


    public SectionBuffer getSectionBuffer(int y){
        int realY = y >> 4;
        if (sectionBuffers[realY] == null){
            sectionBuffers[realY] = new DefaultSectionBuffer(this, realY, 0);
        }
        return sectionBuffers[realY];
    }

    public int getIndex(int x, int y, int z){
        return ((y & 0xf) << 8) | (z << 4) | x;
    }

    @Override
    public String toString() {
        return "DefaultChunkBuffer{" +
                "x=" + x +
                ", z=" + z +
                ", worldBuffer=" + worldBuffer +
                '}';
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultChunkBuffer that = (DefaultChunkBuffer) o;
        return x == that.x && z == that.z && worldBuffer.equals(that.worldBuffer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z, worldBuffer);
    }

}
