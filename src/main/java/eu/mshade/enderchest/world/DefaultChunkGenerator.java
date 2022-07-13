package eu.mshade.enderchest.world;

import eu.mshade.enderchest.world.noise.SimplexOctaveGenerator;
import eu.mshade.enderframe.item.Material;
import eu.mshade.enderframe.metadata.world.WorldMetadataType;
import eu.mshade.enderframe.world.Chunk;
import eu.mshade.enderframe.world.ChunkGenerator;
import eu.mshade.enderframe.world.World;

public class DefaultChunkGenerator implements ChunkGenerator {

    private SimplexOctaveGenerator simplexOctaveGenerator;
    private final World world;

    public DefaultChunkGenerator(World world) {
        this.world = world;
        Long seed = world.getMetadataKeyValueBucket().getValueOfMetadataKeyValue(WorldMetadataType.SEED, Long.class);
        this.simplexOctaveGenerator = new SimplexOctaveGenerator(seed, 8);
        this.simplexOctaveGenerator.setScale(0.005D);
    }

    @Override
    public void generate(Chunk chunk) {
        for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int i = 0; i < 4; i++) {
                        chunk.setBlock(x, i, z, Material.GRASS);

                    }
                    /*
                    int height = (int) (simplexOctaveGenerator.noise(chunk.getX() * 16 + x, chunk.getZ() * 16 + z, 0.5D, 0.5D) * 15D + 50D);
                    for (int i = 0; i < (Math.min(height, 255)); i++) {
                        chunk.setBlock(x, i, z, Material.GRASS);
                    }

                     */

            }
        }
    }
}
