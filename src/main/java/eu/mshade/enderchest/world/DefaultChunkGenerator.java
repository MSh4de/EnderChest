package eu.mshade.enderchest.world;

import eu.mshade.enderchest.world.noise.SimplexOctaveGenerator;
import eu.mshade.enderframe.world.ChunkBuffer;
import eu.mshade.enderframe.world.ChunkGenerator;
import eu.mshade.enderframe.world.WorldBuffer;

public class DefaultChunkGenerator implements ChunkGenerator {

    private SimplexOctaveGenerator simplexOctaveGenerator;
    private final WorldBuffer worldBuffer;

    public DefaultChunkGenerator(WorldBuffer worldBuffer) {
        this.worldBuffer = worldBuffer;
        this.simplexOctaveGenerator = new SimplexOctaveGenerator(worldBuffer.getWorldLevel().getSeed(), 8);
        this.simplexOctaveGenerator.setScale(0.005D);
    }

    @Override
    public void generate(ChunkBuffer chunkBuffer) {
        for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int height = (int) (simplexOctaveGenerator.noise(chunkBuffer.getX() * 16 + x, chunkBuffer.getZ() * 16 + z, 0.5D, 0.5D) * 15D + 50D);
                    for (int i = 0; i < (Math.min(height, 255)); i++) {
                        chunkBuffer.setBlock(x, i, z, 2);
                    }

            }
        }
    }
}
