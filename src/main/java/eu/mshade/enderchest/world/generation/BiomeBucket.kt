package eu.mshade.enderchest.world.generation

import eu.mshade.enderchest.world.noise.SimplexOctaveGenerator
import eu.mshade.enderframe.item.Material

class BiomeBucket(seed: Long) {

    private val biomes = mutableListOf<Biome>()
    private val simplexNoiseGenerator = SimplexOctaveGenerator(seed, 8)


    init {
        simplexNoiseGenerator.setScale(0.005)

        biomes.add(BorderBiome(seed))
        biomes.add(PlainsBiome(seed))
//        biomes.add(SandBiome(seed))
        biomes.add(SnowBiome(seed))
        biomes.add(IslandBiome(seed))
    }

    fun addBiome(biome: Biome) {
        biomes.add(biome)
    }

    fun getBiome(x: Int, z: Int, chunkX: Int, chunkZ: Int): Biome {
        val noise = simplexNoiseGenerator.noise((chunkX * 16 + x).toDouble(), (chunkZ * 16 + z).toDouble(), 0.5, 0.5) * 0.5 + 0.5
        return biomes[((noise * (biomes.size - 1)).toInt())]
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

class SandBiome(seed: Long): Biome(seed, 1){

    init {

        temperatures.add(Material.SAND.toBlock())
        temperatures.add(Material.RED_SAND.toBlock())
        temperatures.add(Material.SANDSTONE.toBlock())
    }

/*    override fun getHeight(x: Int, z: Int, chunkX: Int, chunkZ: Int): Double {
        return perlinNoiseGenerator.noise((chunkX * 16 + x).toDouble(), (chunkZ * 16 + z).toDouble(), octave, 0.5, 0.5) * 10 + 30
    }*/

}

class PlainsBiome(seed: Long): Biome(seed, 12){

    init {

        heightOffset = 50
        amplitude = 10.0
        smoothness = 5.0
        roughness = 1.0



        temperatures.add(Material.DIRT.toBlock())
        temperatures.add(Material.GRASS.toBlock())
        temperatures.add(Material.STONE.toBlock())
    }

}

class SnowBiome(seed: Long): Biome(seed, 6){

    init {
        heightOffset = 90
        amplitude = 30.0
        smoothness = 2.0
        roughness = 75.0


        temperatures.add(Material.SNOW.toBlock())
        temperatures.add(Material.WHITE_WOOL.toBlock())
    }

}

class IslandBiome(seed: Long): Biome(seed, 8) {

    init {
        heightOffset = 50
        amplitude = 8.0
        smoothness = 128.0
        roughness = 2.0

        temperatures.add(Material.SAND.toBlock())
        temperatures.add(Material.GRASS.toBlock())
    }
}

class BorderBiome(seed: Long): Biome(seed, 8) {

    init {
        heightOffset = 30
        amplitude = 8.0
        smoothness = 128.0
        roughness = 2.0

        temperatures.add(Material.AIR.toBlock())
    }
}