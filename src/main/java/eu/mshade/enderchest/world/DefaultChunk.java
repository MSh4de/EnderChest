package eu.mshade.enderchest.world;

import eu.mshade.enderframe.Agent;
import eu.mshade.enderframe.item.Material;
import eu.mshade.enderframe.item.MaterialKey;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.enderframe.world.ChunkStatus;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.block.Block;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.enderframe.world.chunk.Palette;
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
            Section section = sections[i];
            if (section != null && section.getRealBlock() != 0) {
                sectionBitmask |= 1 << i;
            }
        }
        return sectionBitmask;
    }


    @Override
    public Block getBlock(int x, int y, int z) {
        Section section = getSection(y);
        int blockIndex = getBlockIndex(x, y, z);
        return section.getBlock(blockIndex);
    }

    @Override
    public void setBlock(int x, int y, int z, Block block) {
        if (y < 0 || y > 255) return;

        Section section = getSection(y);
        Palette palette = section.getPalette();
        int index = getBlockIndex(x, y, z);
        Block targetBlock = section.getBlock(index);

        if (block.equals(targetBlock)) return;


        if (targetBlock != null) {
            Integer blockId = palette.getId(targetBlock);
            if (blockId != null) {
                palette.removeCount(blockId);
                int blockCount = palette.getCount(blockId);
                if (blockCount <= 0) palette.deleteBlock(blockId);
            }
        }

        Integer blockId = palette.getId(block);
        if (blockId == null) {
            blockId = section.getUniqueId().getFreeId();
            palette.setBlock(blockId, block);
        }
        palette.addCount(blockId);
        section.getBlocks()[index] = blockId;


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
            Block block = getBlock(x, i, z);
            if (block == null) continue;
            if (sections[i >> 4] != null && block.getMaterialKey() != Material.AIR) {
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
            sections[realY] = new DefaultSection(this, realY);
        }
        return sections[realY];
    }

    @Override
    public void addWatcher(Agent agent) {
        agents.add(agent);
        if (chunkStateStore.getChunkStatus() != ChunkStatus.LOADED) {
            chunkStateStore.setChunkStatus(ChunkStatus.LOADED);
        }
    }

    @Override
    public void removeWatcher(Agent agent) {
        agents.remove(agent);
    }

    @Override
    public Collection<Agent> getWatchers() {
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

