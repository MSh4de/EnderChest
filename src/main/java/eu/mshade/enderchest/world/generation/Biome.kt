package eu.mshade.enderchest.world.generation

import eu.mshade.enderchest.world.noise.PerlinNoiseGenerator
import eu.mshade.enderframe.item.MaterialKey
import kotlin.math.pow

abstract class Biome(seed: Long) {

    private val perlinNoiseGenerator = PerlinNoiseGenerator(seed)

    abstract val octaves: Double
    abstract val amplitude: Double
    abstract val smoothness: Double
    abstract val heightOffset: Double
    abstract val roughness: Double

    abstract val topBlock: MaterialKey
    abstract val secondBlock: MaterialKey
    abstract val thirdBlock: MaterialKey
    abstract val miscBlock: MaterialKey

    abstract val secondLayerHeight: Int

    /*    fun getTemperature(x: Int, z: Int, chunkX: Int, chunkZ: Int): Block {
            val noise = simplexOctaveGenerator.noise((chunkX * 16 + x).toDouble(), (chunkZ * 16 + z).toDouble(), 0.5, 0.5) * 0.5 + 0.5
            return temperatures[(noise * (temperatures.size - 1)).toInt()]
        }*/

    open fun getHeight(x: Int, z: Int, chunkX: Int, chunkZ: Int): Double {
        val nx = (chunkX * 16 + x).toDouble()
        val nz = (chunkZ * 16 + z).toDouble()

        var totalValue = 0.0

        for (i in 0 until octaves.toInt()-1) {
            val frequency = 2.0.pow(i.toDouble())
            val amplitude = roughness.pow(i.toDouble())
            totalValue += perlinNoiseGenerator.noise(
                nx * frequency / smoothness,
                nz * frequency / smoothness * amplitude
            )
        }


        val `val` = (totalValue / 2.1 + 1.2) * amplitude + heightOffset


        return if (`val` > 0) `val` else 1.0
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