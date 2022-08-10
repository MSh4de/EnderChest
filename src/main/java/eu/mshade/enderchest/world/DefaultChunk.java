package eu.mshade.enderchest.world;

import eu.mshade.enderframe.Agent;
import eu.mshade.enderframe.item.MaterialKey;
import eu.mshade.enderframe.world.ChunkStatus;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.enderframe.world.chunk.Section;
import eu.mshade.enderframe.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;


public class DefaultChunk extends Chunk {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultChunk.class);

    public static final int WIDTH = 16, HEIGHT = 16, DEPTH = 256, SEC_DEPTH = 16;

    private final Queue<Agent> agents = new ConcurrentLinkedQueue<>();
    private byte[] biomes;

    public DefaultChunk(int x, int z, World world, byte[] biomes) {
        super(x, z, world);
        this.biomes = biomes;
    }

    public DefaultChunk(int x, int z, World world) {
        this(x, z, world, new byte[256]);
    }

    @Override
    public int getBitMask() {
        int sectionBitmask = 0;
        for (int i = 0; i < sections.length; i++) {
            if (sections[i] != null) {
                sectionBitmask |= 1 << i;
            }
        }
        return sectionBitmask;
    }


    @Override
    public int getBlock(int x, int y, int z) {
        Section section = getSection(y);
        return section.getBlocks()[getBlockIndex(x, y, z)];
    }

    @Override
    public void setBlock(int x, int y, int z, MaterialKey materialKey) {
        if (y < 0 || y > 255) return;

        Section section = getSection(y);
        int index = getBlockIndex(x, y, z);
        int lastBlock = section.getBlocks()[index];

        // check if the block is already set to the same value
        //if (lastBlock == materialKey.getId()) return;

        if (materialKey.getId() == 0 && lastBlock != 0) {
            int i = section.getRealBlock() - 1;
            section.setRealBlock(Math.max(i, 0));
        } else section.setRealBlock(section.getRealBlock() + 1);
        section.getBlocks()[index] = materialKey.getId();

        this.getChunkStateStore().interact();

    }

    @Override
    public byte getBlockLight(int x, int y, int z) {
        Section section = getSection(y);
        return section.getBlockLight().get(getBlockIndex(x, y, z));
    }

    @Override
    public void setBlockLight(int x, int y, int z, byte light) {
        Section section = getSection(y);
        section.getBlockLight().set(getBlockIndex(x, y, z), light);

        this.getChunkStateStore().interact();
    }

    @Override
    public byte getSkyLight(int x, int y, int z) {
        Section section = getSection(y);
        return section.getSkyLight().get(getBlockIndex(x, y, z));
    }

    @Override
    public void setSkyLight(int x, int y, int z, byte light) {
        Section section = getSection(y);
        section.getSkyLight().set(getBlockIndex(x, y, z), light);

        this.getChunkStateStore().interact();
    }

    @Override
    public void setBiome(int x, int z, int biome) {
        biomes[getBiomeIndex(x, z)] = (byte) biome;
        this.getChunkStateStore().interact();
    }

    @Override
    public int getBiome(int x, int z) {
        return biomes[getBiomeIndex(x, z)] & 0xFF;
    }

    @Override
    public byte[] getBiomes() {
        return biomes;
    }

    @Override
    public int getHighest(int x, int z) {
        for (int i = 255; i > 0; i--) {
            if (sections[i >> 4] != null && getBlock(x, i, z) != 0) {
                return i;
            }
        }
        return 0;
    }



    public void setBiomes(byte[] biomes) {
        this.biomes = biomes;
    }


    @Override
    public Section getSection(int y) {
        int realY = y >> 4;
        if (sections[realY] == null) {
            sections[realY] = new DefaultSection(this, realY, 0);
        }
        return sections[realY];
    }

    @Override
    public void addWatcher(Agent agent) {
        agents.add(agent);
        if(chunkStateStore.getChunkStatus() != ChunkStatus.LOADED){
            chunkStateStore.setChunkStatus(ChunkStatus.LOADED);
        }
    }

    @Override
    public void removeWatcher(Agent agent) {
        agents.remove(agent);
    }

    @Override
    public Collection<Agent> getWatching() {
        return agents;
    }

    @Override
    public boolean isWatching(Agent agent) {
        return agents.contains(agent);
    }

    @Override
    public void notify(Consumer<Agent> sessionWrapperConsumer) {
        for (Agent agent : agents) {
            sessionWrapperConsumer.accept(agent);
        }
    }

    public int getBiomeIndex(int x, int z) {
        return (z & 0xF) * WIDTH + (x & 0xF);
    }

    public int getBlockIndex(int x, int y, int z) {
        return ((y & 0xf) << 8) | ((z & 0x0f) << 4) | (x & 0x0f);
    }

    @Override
    public String toString() {
        return "DefaultChunk{" +
                "x=" + x +
                ", z=" + z +
                ", players=" + agents +
                ", world=" + world +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultChunk that = (DefaultChunk) o;
        return id == that.id && world.equals(that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, world);
    }
}

