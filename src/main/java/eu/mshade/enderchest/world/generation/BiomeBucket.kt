package eu.mshade.enderchest.world.generation

import eu.mshade.enderchest.world.noise.PerlinNoiseGenerator
import eu.mshade.enderchest.world.noise.SimplexOctaveGenerator
import eu.mshade.enderframe.item.Material

class BiomeBucket(seed: Long) {

    private val biomes = mutableListOf<Biome>()
    private val perlinNoiseGenerator = PerlinNoiseGenerator(seed)


    init {
//        simplexNoiseGenerator.setScale(0.005)
    }

    fun addBiome(biome: Biome) {
        biomes.add(biome)
    }

    /*    fun getBiome(x: Int, z: Int, chunkX: Int, chunkZ: Int): Biome {
            val noise = simplexNoiseGenerator.noise((chunkX * 16 + x).toDouble(), (chunkZ * 16 + z).toDouble(), 0.5, 0.5) * 0.5 + 0.5
            return biomes[((noise * (biomes.size - 1)).toInt())]
        }*/
    fun getBiome(x: Int, z: Int, chunkX: Int, chunkZ: Int): Biome {
        val noiseScale = 0.1
        val noiseX = (chunkX * 16 + x).toDouble() * noiseScale
        val noiseZ = (chunkZ * 16 + z).toDouble() * noiseScale

        val noiseValue = perlinNoiseGenerator.noise(noiseX, noiseZ)
        val normalizedValue = (noiseValue + 1.0) / 2.0 // Normaliser la valeur du bruit entre 0 et 1

        if (normalizedValue < 0.3) return biomes[0]
        return biomes[1]
    }


    fun getHeight(x: Int, z: Int, chunkX: Int, chunkZ: Int): Double {
        return getBiome(x, z, chunkX, chunkZ).getHeight(x, z, chunkX, chunkZ)
    }


    fun getHeightMap(chunkX: Int, chunkZ: Int): Array<IntArray> {
        val chunkMap = Array(16) { IntArray(16) }
        val bottomLeft = this.getHeight(0, 0, chunkX, chunkZ)
        val bottomRight = this.getHeight(16, 0, chunkX, chunkZ)
        val topLeft = this.getHeight(0, 16, chunkX, chunkZ)
        val topRight = this.getHeight(16, 16, chunkX, chunkZ)
        for (x in 0..15) for (z in 0..15) {
            chunkMap[x][z] =
                bilinearInterpolation(bottomLeft, topLeft, bottomRight, topRight, 0, 16, 0, 16, x, z)
                    .toInt()
        }
        return chunkMap
    }

    fun bilinearInterpolation(
        bottomLeft: Double,
        topLeft: Double,
        bottomRight: Double,
        topRight: Double,
        xMin: Int,
        xMax: Int,
        zMin: Int,
        zMax: Int,
        x: Int,
        z: Int
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


class ForestBiome(seed: Long) : Biome(seed) {
    override val octaves = 8.0
    override val amplitude = 20.0
    override val smoothness = 100.0
    override val heightOffset = 1.0
    override val roughness = 1.0

    override val topBlock = Material.GRASS_BLOCK
    override val secondBlock = Material.DIRT
    override val thirdBlock = Material.STONE
    override val miscBlock = Material.GRASS_BLOCK

    override val secondLayerHeight = 3
}

// Implémenter un autre exemple de biome spécifique (désert)
class DesertBiome(seed: Long) : Biome(seed) {
    override val octaves = 6.0
    override val amplitude = 30.0
    override val smoothness = 1.0
    override val heightOffset = 10.0
    override val roughness = 0.1

    override val topBlock = Material.SAND
    override val secondBlock = Material.SAND
    override val thirdBlock = Material.STONE
    override val miscBlock = Material.STONE

    override val secondLayerHeight = 3
}

class FrozenBiome(seed: Long) : Biome(seed) {
    override val octaves = 8.0
    override val amplitude = 20.0
    override val smoothness = 100.0
    override val heightOffset = 1.0
    override val roughness = 1.0

    override val topBlock = Material.SNOW_BLOCK
    override val secondBlock = Material.SNOW_BLOCK
    override val thirdBlock = Material.STONE
    override val miscBlock = Material.STONE

    override val secondLayerHeight = 3
}