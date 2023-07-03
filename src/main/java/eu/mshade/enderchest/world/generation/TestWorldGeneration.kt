package eu.mshade.enderchest.world.generation

import eu.mshade.enderframe.world.chunk.Chunk
import eu.mshade.enderframe.world.chunk.ChunkGenerator
import java.util.*
import java.util.concurrent.ThreadLocalRandom


class TestWorldGeneration: ChunkGenerator {

    private val biomeBucket : BiomeBucket

    init {
        biomeBucket = BiomeBucket(4975988339999789512L)
        biomeBucket.addBiome(DesertBiome(4975988339999789512L))
        biomeBucket.addBiome(ForestBiome(4975988339999789512L))
    }

    override fun generate(chunk: Chunk) {

        val heightMap = biomeBucket.getHeightMap(chunk.x, chunk.z)

        for (x in 0..15) {
            for (z in 0..15) {
                val currentHeight = heightMap[x][z]
                val gen = biomeBucket.getBiome(x, z, chunk.x, chunk.z)
//                biome.setBiome(x, currentHeight, z, gen.getBiome())
                if (ThreadLocalRandom.current().nextInt(20) === 2) {
                    chunk.setBlock(x, currentHeight, z, gen.miscBlock)
                } else chunk.setBlock(x, currentHeight, z, gen.topBlock)
                for (y in currentHeight - 1 downTo currentHeight - gen.secondLayerHeight) {
                    chunk.setBlock(x, y, z, gen.secondBlock)
                }
                for (y in currentHeight - gen.secondLayerHeight - 1 downTo 200 - heightMap[x][z] + 1) {
                    chunk.setBlock(x, y, z, gen.thirdBlock)
                }
            }
        }

    }
}