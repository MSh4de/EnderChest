import eu.mshade.enderframe.world.chunk.ChunkStateStore;

import java.util.ArrayList;

public class TestStore {

    public static void main(String[] args) {
        int chunkX = 0, chunkZ = 0;

        int i = 0;
        for (int x = chunkX - 5; x <= chunkX + 5; x++) {
            for (int z = chunkZ - 5; z <= chunkZ + 5; z++) {
                if ((chunkX - x) * (chunkX - x) + (chunkZ - z) * (chunkZ - z) <= 5 * 5) {
                    i++;
                }
            }
        }
        System.out.println(i);
    }

}
