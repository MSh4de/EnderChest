package eu.mshade.enderchest.world;

import eu.mshade.enderchest.world.noise.SimplexOctaveGenerator;
import eu.mshade.enderframe.item.Material;
import eu.mshade.enderframe.world.WorldMetadataType;
import eu.mshade.enderframe.world.block.Block;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.enderframe.world.chunk.ChunkGenerator;
import eu.mshade.enderframe.world.World;

public class DefaultChunkGenerator implements ChunkGenerator {

    private SimplexOctaveGenerator simplexOctaveGenerator;
    private final World world;

    public DefaultChunkGenerator(World world) {
        this.world = world;
        Long seed = (Long) world.getMetadatas().getMetadataKeyValue(WorldMetadataType.SEED).getMetadataValue();
        this.simplexOctaveGenerator = new SimplexOctaveGenerator(seed, 8);
        this.simplexOctaveGenerator.setScale(0.005D);
    }

    @Override
    public void generate(Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Block block = Material.GRASS_BLOCK.toBlock();
                    for (int i = 0; i < 4; i++) {
                        chunk.setBlock(x, i, z, block);
                    }


/*                    int height = (int) (simplexOctaveGenerator.noise(chunk.getX() * 16 + x, chunk.getZ() * 16 + z, 0.5D, 0.5D) * 15D + 50D);
                    for (int i = 0; i < (Math.min(height, 255)); i++) {
                        chunk.setBlock(x, i, z, block);
                    }*/


            }
        }
    }
}
