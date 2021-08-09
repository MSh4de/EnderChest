package eu.mshade.enderchest.world;

import eu.mshade.enderframe.world.ChunkBuffer;
import eu.mshade.enderframe.world.NibbleArray;
import eu.mshade.enderframe.world.SectionBuffer;

public class DefaultSectionBuffer implements SectionBuffer {

    private ChunkBuffer chunkBuffer;
    private int y;
    private int realBlock;
    private int[] blocks;
    private NibbleArray data;
    private NibbleArray blockLight;
    private NibbleArray skyLight;

    public DefaultSectionBuffer(ChunkBuffer chunkBuffer, int y, int realBlock) {
        this(chunkBuffer, y, realBlock, new int[4096], NibbleArray.allocate(4096), NibbleArray.allocate(4096), NibbleArray.allocate(4096));
        blockLight.fill((byte) 15);
    }

    public DefaultSectionBuffer(ChunkBuffer chunkBuffer, int y, int realBlock, int[] blocks, NibbleArray data, NibbleArray blocksLight, NibbleArray skyLight) {
        this.chunkBuffer = chunkBuffer;
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
    public ChunkBuffer getChunkBuffer() {
        return chunkBuffer;
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
                "chunkBuffer=" + chunkBuffer +
                ", y=" + y +
                ", realBlock=" + realBlock +
                '}';
    }
}
