package eu.mshade.enderchest.world;

import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderchest.world.virtual.VirtualChunk;
import eu.mshade.enderchest.world.virtual.VirtualSection;
import eu.mshade.enderchest.world.virtual.VirtualSectionStatus;
import eu.mshade.enderframe.Agent;
import eu.mshade.enderframe.item.Material;
import eu.mshade.enderframe.virtualserver.VirtualWorld;
import eu.mshade.enderframe.world.ChunkStatus;
import eu.mshade.enderframe.world.block.Block;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.enderframe.world.chunk.EmptySection;
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
    private static final Block AIR = Material.AIR.toBlock();

    private final Queue<Agent> agents = new ConcurrentLinkedQueue<>();
    private byte[] biomes = new byte[256];

    public DefaultChunk(int x, int z, World world) {
        super(x, z, world);
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        int sectionIndex = y >> 4;

        Section section = getSection(sectionIndex);
        Block block;
        if (section != null) {
            int blockIndex = getBlockIndex(x, y, z);
            block = section.getBlock(blockIndex);
        }else {
            block = AIR;
        }

        return block;
    }

    @Override
    public void setBlock(int x, int y, int z, Block block) {
        if (y < 0 || y > 255) return;
        int sectionIndex = y >> 4;

        Section section = getSectionOrCreate(sectionIndex);
        if (this.getChunkStateStore().getChunkStatus() != ChunkStatus.PREPARE_TO_LOAD) {
            try {
                Collection<VirtualWorld> virtualWorldsFrom = EnderChest.INSTANCE.getVirtualWorldManager().getVirtualWorldsFrom(getWorld());
                for (VirtualWorld virtualWorld : virtualWorldsFrom) {
                    VirtualChunk virtualChunk = (VirtualChunk) virtualWorld.getChunk(getX(), getZ()).get();
                    Section virtualSection = virtualChunk.getSection(sectionIndex);
                    if(virtualSection == null){
                        virtualChunk.getSections()[sectionIndex] = virtualChunk.copySection(section);
                        virtualSection = virtualChunk.getSection(sectionIndex);
                    }
                    if (virtualSection instanceof VirtualSection vs) {
                        vs.setVirtualSectionStatus(VirtualSectionStatus.DIFFERENT);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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
        int sectionIndex = y >> 4;
        Section section = getSectionOrCreate(sectionIndex);
        return section.getBlockLight().get(getBlockIndex(x, y, z));
    }

    @Override
    public void setBlockLight(int x, int y, int z, byte light) {
        int sectionIndex = y >> 4;
        Section section = getSectionOrCreate(sectionIndex);
        section.getBlockLight().set(getBlockIndex(x, y, z), light);

        this.getChunkStateStore().interact();
    }

    @Override
    public byte getSkyLight(int x, int y, int z) {
        int sectionIndex = y >> 4;
        Section section = getSectionOrCreate(sectionIndex);
        return section.getSkyLight().get(getBlockIndex(x, y, z));
    }

    @Override
    public void setSkyLight(int x, int y, int z, byte light) {
        int sectionIndex = y >> 4;
        Section section = getSectionOrCreate(sectionIndex);
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
    public void setBiomes(byte[] biomes) {
        this.biomes = biomes;
    }


    @Override
    public Section getSectionOrCreate(int y) {
        if (sections[y] == null) {
            sections[y] = new DefaultSection(this, y);
        }
        return sections[y];
    }

    @Override
    public Section createSection(int y) {
        if (sections[y] != null) {
            throw new IllegalStateException("Section already exists");
        }
        return sections[y] = new DefaultSection(this, y);
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
    public Collection<Agent> getWatcher() {
        return agents;
    }

    @Override
    public boolean isWatching(Agent agent) {
        return agents.contains(agent);
    }

    @Override
    public void notify(Consumer<Agent> agentConsumer) {
        for (Agent agent : agents) {
            agentConsumer.accept(agent);
        }
    }

    @Override
    public String toString() {
        return "DefaultChunk{" + "x=" + x + ", z=" + z + ", players=" + agents + ", world=" + world + '}';
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

