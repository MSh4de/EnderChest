package eu.mshade.enderchest.world;

import eu.mshade.enderframe.world.ChunkBuffer;
import eu.mshade.enderframe.world.NibbleArray;
import eu.mshade.enderframe.world.SectionBuffer;

public class DefaultSectionBuffer implements SectionBuffer {

    private ChunkBuffer chunkBuffer;
    private int y;
    private int realBlock;
    private int[] blocks = new int[4096];
    private final NibbleArray data = NibbleArray.allocate(4096);
    private final NibbleArray blockLight = NibbleArray.allocate(4096);
    private final NibbleArray skyLight = NibbleArray.allocate(4096);

    public DefaultSectionBuffer(ChunkBuffer chunkBuffer, int y, int realBlock) {
        this.chunkBuffer = chunkBuffer;
        this.y = y;
        this.realBlock = realBlock;
        blockLight.fill((byte) 15);
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
