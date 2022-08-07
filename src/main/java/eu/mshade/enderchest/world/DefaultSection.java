package eu.mshade.enderchest.world;

import eu.mshade.enderframe.world.Chunk;
import eu.mshade.enderframe.world.Dimension;
import eu.mshade.enderframe.world.NibbleArray;
import eu.mshade.enderframe.world.Section;

import java.util.Arrays;
import java.util.Objects;

public class DefaultSection implements Section {

    private Chunk chunk;
    private int y;
    private int realBlock;
    private int[] blocks;
    private NibbleArray blockLight;
    private NibbleArray skyLight;

    public DefaultSection(Chunk chunk, int y, int realBlock) {
        this(chunk, y, realBlock, new int[4096], NibbleArray.allocate(4096), NibbleArray.allocate(4096));
        // check if world is overlord(get from metadataKeyValue) and complete skylight
        if (chunk.getWorld().getDimension() == Dimension.OVERWORLD) {
            skyLight.fill((byte) 15);
        }

        blockLight.fill((byte) 15);
    }

    public DefaultSection(Chunk chunk, int y, int realBlock, int[] blocks, NibbleArray blocksLight, NibbleArray skyLight) {
        this.chunk = chunk;
        this.y = y;
        this.realBlock = realBlock;
        this.blocks = blocks;
        this.blockLight = blocksLight;
        this.skyLight = skyLight;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public Chunk getChunk() {
        return chunk;
    }

    @Override
    public int getRealBlock() {
        return realBlock;
    }

    @Override
    public void setRealBlock(int realBlock) {
        this.realBlock = realBlock;
    }

    @Override
    public int[] getBlocks() {
        return blocks;
    }

    @Override
    public NibbleArray getBlockLight() {
        return blockLight;
    }

    @Override
    public NibbleArray getSkyLight() {
        return skyLight;
    }

    public void setBlocks(int[] blocks) {
        this.blocks = blocks;
    }

    @Override
    public String toString() {
        return "DefaultSectionBuffer{" +
                "chunkBuffer=" + chunk +
                ", y=" + y +
                ", realBlock=" + realBlock +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultSection that = (DefaultSection) o;
        return y == that.y && realBlock == that.realBlock && Objects.equals(chunk, that.chunk) && Arrays.equals(blocks, that.blocks) && Objects.equals(blockLight, that.blockLight) && Objects.equals(skyLight, that.skyLight);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(chunk, y, realBlock, blockLight, skyLight);
        result = 31 * result + Arrays.hashCode(blocks);
        return result;
    }
}
