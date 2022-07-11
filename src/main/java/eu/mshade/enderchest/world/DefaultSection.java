package eu.mshade.enderchest.world;

import eu.mshade.enderframe.world.Chunk;
import eu.mshade.enderframe.world.NibbleArray;
import eu.mshade.enderframe.world.Section;

public class DefaultSection implements Section {

    private Chunk chunk;
    private int y;
    private int realBlock;
    private int[] blocks;
    private NibbleArray data;
    private NibbleArray blockLight;
    private NibbleArray skyLight;

    public DefaultSection(Chunk chunk, int y, int realBlock) {
        this(chunk, y, realBlock, new int[4096], NibbleArray.allocate(4096), NibbleArray.allocate(4096), NibbleArray.allocate(4096));
        skyLight.fill((byte) 15);
        blockLight.fill((byte) 15);
    }

    public DefaultSection(Chunk chunk, int y, int realBlock, int[] blocks, NibbleArray data, NibbleArray blocksLight, NibbleArray skyLight) {
        this.chunk = chunk;
        this.y = y;
        this.realBlock = realBlock;
        this.blocks = blocks;
        this.data = data;
        this.blockLight = blocksLight;
        this.skyLight = skyLight;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public Chunk getChunkBuffer() {
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
    public NibbleArray getData() {
        return data;
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
}
