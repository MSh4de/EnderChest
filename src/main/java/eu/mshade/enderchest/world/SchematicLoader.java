package eu.mshade.enderchest.world;

import eu.mshade.enderframe.Agent;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.item.Material;
import eu.mshade.enderframe.item.MaterialKey;
import eu.mshade.enderframe.world.block.Block;
import eu.mshade.enderframe.world.block.BlockTransformerController;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.enderframe.world.Vector;
import eu.mshade.enderframe.world.World;
import eu.mshade.enderman.EndermanMinecraftProtocol;
import eu.mshade.mwork.MWork;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPInputStream;

public class SchematicLoader {

    private static EndermanMinecraftProtocol endermanProtocol = new EndermanMinecraftProtocol();
    private static Logger LOGGER = LoggerFactory.getLogger(SchematicLoader.class);
    private static final Block AIR = Material.AIR.toBlock();
    public static File SCHEMATIC_FOLDER = new File(System.getProperty("user.dir") , "schematics");

    public static void placeSchematic(World world, String schematic, Vector start){
        try {
            placeSchematic(world, new FileInputStream(new File(SCHEMATIC_FOLDER, schematic)), start);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void placeSchematic(World world, InputStream inputStream, Vector start) {
        BlockTransformerController blockTransformerController = endermanProtocol.getBlockTransformerController();
        Block STONE = Material.STONE.toBlock();
        CompletableFuture.runAsync(() -> {

            BinaryTagDriver binaryTagDriver = MWork.INSTANCE.getBinaryTagDriver();
            try {
                Agent schematicAgent = Agent.from("SCHEMATIC");

                long startTime = System.currentTimeMillis();
                GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
                CompoundBinaryTag compoundBinaryTag = binaryTagDriver.readCompoundBinaryTag(new ByteArrayInputStream(gzipInputStream.readAllBytes()));
                short width = compoundBinaryTag.getShort("Width");
                short length = compoundBinaryTag.getShort("Length");
                short height = compoundBinaryTag.getShort("Height");
                byte[] blocks = compoundBinaryTag.getByteArray("Blocks");
                byte[] blocksData = compoundBinaryTag.getByteArray("Data");

                System.out.println("Schematic size: " + width + "x" + length + "x" + height);

                int unknown = 0;
                Set<Chunk> updatedChunks = new HashSet<>();
                
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        for (int z = 0; z < length; z++) {
                            int index = y * width * length + z * width + x;
                            byte blockByIndex = blocks[index];
                            int block = blockByIndex < 0 ? blockByIndex + 256 : blockByIndex;
                            byte blockData = blocksData[index];

                            MaterialKey materialKey;
                            if (block == 0) {
                                materialKey = Material.AIR;
                            } else {
                                materialKey = MaterialKey.from(block, blockData);
                            }
                            Block reverse;
                            if (materialKey != Material.AIR) {
                                reverse = blockTransformerController.reverse(materialKey);

                            if (reverse == null) {
                                unknown++;
                                reverse = STONE;
                            }

/*                                if (reverse == null) {

                                }*/
                            }else {
                                reverse = AIR;
                            }


                            CompletableFuture<Chunk> chunkCompletableFuture = world.getChunk((x + start.getBlockX()) >> 4, (z + start.getBlockZ()) >> 4);
                            Chunk chunk = chunkCompletableFuture.get();
                            updatedChunks.add(chunk);

                            if (!chunk.isWatching(schematicAgent)) {
                                schematicAgent.joinWatch(chunk);
                            }

                            Block finalReverse = reverse;
                            chunk.setBlock(x + start.getBlockX(), y + start.getBlockY(), z + start.getBlockZ(), finalReverse);
                        }
                    }
                }

                System.out.println(unknown + " unknown blocks out of " + blocks.length+ " blocks (" + (unknown / (double) blocks.length * 100) + "%)");
                LOGGER.info("Loaded schematic in " + (System.currentTimeMillis() - startTime) + "ms");

                updatedChunks.forEach(chunk -> {
                    // set middle block into chunk
/*                    int maxY = chunk.getHighest(chunk.getX(), chunk.getZ());
                    // get middle of chunk
                    int middleX = chunk.getX() << 4;
                    int middleZ = chunk.getZ() << 4;

                    chunk.setBlock(chunk.getX() + middleX, maxY, chunk.getZ() + middleZ, Material.RED_WOOL);*/

                    chunk.notify(Player.class, player -> {
                        player.getMinecraftSession().sendUnloadChunk(chunk);
                    });
                });

                updatedChunks.forEach(chunk -> {
                    chunk.notify(Player.class, player -> {
                        player.getMinecraftSession().sendChunk(chunk);
                    });
                    schematicAgent.leaveWatch(chunk);
                });


            } catch (Throwable e) {
                e.printStackTrace();
            }

        });
    }
}
