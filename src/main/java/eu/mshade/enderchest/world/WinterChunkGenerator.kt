package eu.mshade.enderchest.world

import eu.mshade.enderchest.world.generation.BiomeBucket
import eu.mshade.enderframe.world.World
import eu.mshade.enderframe.world.chunk.Chunk
import eu.mshade.enderframe.world.chunk.ChunkGenerator
import java.lang.Exception

class WinterChunkGenerator(val world: World) : ChunkGenerator {

    private val biomeBucket = BiomeBucket(world.seed)

    override fun generate(chunk: Chunk) {
        val chunkX = chunk.x
        val chunkZ = chunk.z

        try {
            val heightMap = biomeBucket.getHeightMap(chunkX, chunkZ)
            for (x in 0..15) {
                for (z in 0..15) {
                    val height = heightMap[x][z]
                    val block = biomeBucket.getBiome(x, z, chunkX, chunkZ).getTemperature(x, z, chunkX, chunkZ)
                    if (height > 255 || height < 0) println(height)
                    for (y in 0..height) {
                        chunk.setBlock(x, y, z, block)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun bilinearInterpolation(
        bottomLeft: Double,
        topLeft: Double,
        bottomRight: Double,
        topRight: Double,
        xMin: Double,
        xMax: Double,
        zMin: Double,
        zMax: Double,
        x: Double,
        z: Double
    ): Double {
        val width = xMax - xMin
        val height = zMax - zMin
        val xDistanceToMaxValue = xMax - x
        val zDistanceToMaxValue = zMax - z
        val xDistanceToMinValue = x - xMin
        val zDistaceToMinValue = z - zMin
        return 1.0 / (width * height) *
                (bottomLeft * xDistanceToMaxValue * zDistanceToMaxValue + bottomRight * xDistanceToMinValue * zDistanceToMaxValue + topLeft * xDistanceToMaxValue * zDistaceToMinValue + topRight * xDistanceToMinValue * zDistaceToMinValue)
    }
}

/*
// Définissez les coordonnées des quatre points les plus proches
float x1, y1, x2, y2;
float q11, q12, q21, q22;

// Définissez les coordonnées de l'emplacement pour lequel vous souhaitez estimer la valeur
float x, y;

// Calculez la valeur estimée en utilisant la formule de bilinéaire interpolation
float x2x1, y2y1, x2x, y2y, yy1, xx1;
x2x1 = x2 - x1;
y2y1 = y2 - y1;
x2x = x2 - x;
y2y = y2 - y;
yy1 = y - y1;
xx1 = x - x1;
float result = 1.0f / (x2x1 * y2y1) * (
    q11 * x2x * y2y +
    q21 * xx1 * y2y +
    q12 * x2x * yy1 +
    q22 * xx1 * yy1
);
*/

/*
 val heights = DoubleArray(256)
        val temperatures = DoubleArray(256)
        for (x in 0..15) {
            for (z in 0..15) {
                val localX = x + chunkX * 16
                val localZ = z + chunkZ * 16
                heights[x * 16 + z] =
                    simplexOctaveGenerator.noise(localX.toDouble(), localZ.toDouble(), 0.5, 0.5) * 30 + 50
                temperatures[x * 16 + z] =
                    simplexOctaveGenerator.noise(localX.toDouble(), localZ.toDouble(), 0.5, 0.5) * 0.5 + 0.5
            }
        }

        for (x in 0..15) {
            for (z in 0..15) {
                for (y in 0..255) {
                    val temperature = temperatures[x * 16 + z]
                    val height = heights[x * 16 + z]
                    val localX = x + chunkX * 16
                    val localZ = z + chunkZ * 16

                    if (temperature < 0.2) {
                        if (y >= height) {
                            chunk.setBlock(localX, y, localZ, Material.ICE.toBlock())
                        } else {
                            chunk.setBlock(localX, y, localZ, Material.SNOW.toBlock())
                        }
                    }
                    // Si la température est moyenne, on place de la terre ou de la roche
                    else if (temperature < 0.5) {
                        if (y >= height - 3 && y <= height) {
                            chunk.setBlock(localX, y, localZ, Material.STONE.toBlock())
                        } else if (y < height) {
                            chunk.setBlock(localX, y, localZ, Material.DIRT.toBlock())
                        }
                    }
                    // Si la température est chaude, on place de l'herbe ou de la terre
                    else {
                        if (y == height.toInt()) {
                            chunk.setBlock(localX, y, localZ, Material.GRASS.toBlock())
                        } else if (y < height) {
                            chunk.setBlock(localX, y, localZ, Material.DIRT.toBlock())
                        }

                    }
                }
            }
        }
 */
