package eu.mshade.enderchest.world;

import eu.mshade.enderframe.item.MaterialKey;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.enderframe.world.Vector;
import eu.mshade.enderframe.world.World;
import eu.mshade.enderman.wrapper.EndermanMaterialWrapper;
import eu.mshade.mwork.MWork;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPInputStream;

public class SchematicLoader {

    private static EndermanMaterialWrapper endermanMaterialWrapper = new EndermanMaterialWrapper();
    private static Logger LOGGER = LoggerFactory.getLogger(SchematicLoader.class);

    public static void placeSchematic(World world, InputStream inputStream, Vector start){
        BinaryTagDriver binaryTagDriver = MWork.get().getBinaryTagDriver();
        try {
            long startTime = System.currentTimeMillis();
            GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
            CompoundBinaryTag compoundBinaryTag = binaryTagDriver.readCompoundBinaryTag(gzipInputStream);
            short width = compoundBinaryTag.getShort("Width");
            short length = compoundBinaryTag.getShort("Length");
            short height = compoundBinaryTag.getShort("Height");
            byte[] blocks = compoundBinaryTag.getByteArray("Blocks");
            byte[] blocksData = compoundBinaryTag.getByteArray("Data");

            int unknown = 0;
            Set<Chunk> updatedChunks = new HashSet<>();
            // with, legnth, height by center
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < length; z++) {
                        int index = y * width * length + z * width + x;
                        byte blockByIndex = blocks[index];
                        int block = blockByIndex < 0 ? blockByIndex + 256 : blockByIndex;
                        byte blockData = blocksData[index];

                        MaterialKey materialKey = MaterialKey.from(block, blockData);
                        MaterialKey reverse = endermanMaterialWrapper.reverse(materialKey);
                        if (reverse == null) {
                            reverse = endermanMaterialWrapper.reverse(MaterialKey.from(block));
                        }

                        if (reverse == null) {
                            unknown++;
                            continue;
                        }

/*                        if (reverse == null) {
                            reverse = Material.STONE;
                        }*/
                        CompletableFuture<Chunk> chunkCompletableFuture = world.getChunk((x + start.getBlockX()) >> 4, (z + start.getBlockZ()) >> 4);
                        Chunk chunk = chunkCompletableFuture.get();
                        updatedChunks.add(chunk);
                        chunk.setBlock(x + start.getBlockX(), y + start.getBlockY(), z + start.getBlockZ(), reverse);
                    }
                }
            }

            System.out.println(unknown + " unknown blocks out of " + blocks.length);
            LOGGER.info("Loaded schematic in " + (System.currentTimeMillis() - startTime) + "ms");

            updatedChunks.forEach(chunk -> {

                chunk.getViewers().forEach(player -> {
                    player.getSessionWrapper().sendUnloadChunk(chunk);
                });


            });

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            updatedChunks.forEach(chunk -> {

                chunk.getViewers().forEach(player -> {
                    player.getSessionWrapper().sendChunk(chunk);
                });

            });
        }catch (IOException e){
            e.printStackTrace();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
