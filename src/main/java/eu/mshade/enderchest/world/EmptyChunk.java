package eu.mshade.enderchest.world;

import eu.mshade.enderframe.world.World;

public class EmptyChunk extends DefaultChunk {

    public EmptyChunk(int x, int z, World world, byte[] biomes) {
        super(x, z, world, biomes);
    }

    public EmptyChunk(int x, int z, World world) {
        super(x, z, world);
    }
}
