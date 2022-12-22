package eu.mshade.enderchest.world.generation

import eu.mshade.enderchest.world.noise.PerlinNoiseGenerator
import eu.mshade.enderchest.world.noise.SimplexNoise
import eu.mshade.enderchest.world.noise.SimplexOctaveGenerator
import eu.mshade.enderframe.world.block.Block
import kotlin.math.pow

abstract class Biome(seed: Long, val octave: Int = 4) {

    val perlinNoiseGenerator = SimplexNoise(seed)
    private val simplexOctaveGenerator = SimplexOctaveGenerator(seed, octave)

    var amplitude = 1.0
    var heightOffset = 0
    var smoothness = 1.0
    var roughness = 1.0

    init {
        simplexOctaveGenerator.setScale(0.005)
    }

    val temperatures = mutableListOf<Block>()


    fun getTemperature(x: Int, z: Int, chunkX: Int, chunkZ: Int): Block {
        val noise = simplexOctaveGenerator.noise((chunkX * 16 + x).toDouble(), (chunkZ * 16 + z).toDouble(), 0.5, 0.5) * 0.5 + 0.5
        return temperatures[(noise * (temperatures.size - 1)).toInt()]
    }

    open fun getHeight(x: Int, z: Int, chunkX: Int, chunkZ: Int): Double {
        val nx = (chunkX * 16 + x).toDouble()
        val nz = (chunkZ * 16 + z).toDouble()

        var noise = 0.0
        for (i in 0 until octave) {
            val frequency = 2.0.pow(i)
            noise += perlinNoiseGenerator.noise(nx * frequency / smoothness, nz * frequency / smoothness * roughness.pow(i))
        }

        noise = (noise / octave) * amplitude + heightOffset


        return if (noise > 0) noise else 0.0

    }

/*        var noise = 0.0
        for (i in 0 until octave) {
            val frequency = 2.0.pow(i)
            val amplitude = roughness.pow(i)
            noise += perlinNoiseGenerator.noise(nx * frequency / smoothness, nz * frequency / smoothness * amplitude)
        }

        noise = (noise / octave + 1.2) * amplitude + heightOffset

        // Normalisation
        val minValue = -1.0
        val maxValue = 1.0
        val range = maxValue - minValue
        noise = (noise - minValue) / range

        return if (noise > 0) noise else 0.0*/


}