import eu.mshade.enderframe.item.Material
import eu.mshade.enderframe.item.MaterialKey
import eu.mshade.enderframe.world.block.Block
import eu.mshade.enderman.EndermanMinecraftProtocol
import eu.mshade.mwork.MWork
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.nio.file.Path
import java.util.zip.GZIPInputStream

var SCHEMATIC_FOLDER = File(Path.of(System.getProperty("user.dir")).parent.toFile(), "schematics")

class MaterialTest {

    val endermanProtocol = EndermanMinecraftProtocol()
    val blockTransformerController = endermanProtocol.blockTransformerController

    @Test
    fun `test material reverse wrap`() {
        val materialKey = MaterialKey.from(1, 0)
        val reverse = blockTransformerController.reverse(materialKey)
        assert(reverse != null)
        println(reverse!!.getMaterial() == Material.STONE)
    }

    @Test
    fun `test blockTransformer with schematic`() {
        val fileInputStream = FileInputStream(File(SCHEMATIC_FOLDER, "id9.schematic"))

        val binaryTagDriver = MWork.getBinaryTagDriver()

        val gzipInputStream = GZIPInputStream(fileInputStream)
        val compoundBinaryTag =
            binaryTagDriver.readCompoundBinaryTag(ByteArrayInputStream(gzipInputStream.readAllBytes()))
        val width = compoundBinaryTag.getShort("Width")
        val length = compoundBinaryTag.getShort("Length")
        val height = compoundBinaryTag.getShort("Height")
        val blocks = compoundBinaryTag.getByteArray("Blocks")
        val blocksData = compoundBinaryTag.getByteArray("Data")

        var unknown = 0

        val unknownTypes = mutableSetOf<MaterialKey>()

        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until length) {
                    val index: Int = y * width * length + z * width + x
                    val blockByIndex = blocks[index]
                    val block = if (blockByIndex < 0) blockByIndex + 256 else blockByIndex.toInt()
                    val blockData = blocksData[index]

                    var materialKey: MaterialKey
                    materialKey = if (block == 0) {
                        Material.AIR
                    } else {
                        MaterialKey.from(block, blockData.toInt())
                    }
                    var reverse: Block?
                    if (materialKey !== Material.AIR) {
                        reverse = blockTransformerController.reverse(materialKey)
                        if (reverse == null) {
                            reverse = blockTransformerController.reverse(MaterialKey.from(block))
                            if (reverse == null) {
                                unknown++
                                unknownTypes.add(materialKey)
                            }
                        }
                    }

                }
            }
        }
        println("unknown: $unknown")
        println("unknownTypes: $unknownTypes")
        assert(unknownTypes.size < 10)
    }

}
/*
fun main() {
    val endermanProtocol = EndermanMinecraftProtocol()
    val blockTransformerController = endermanProtocol.blockTransformerController
    val fileInputStream = FileInputStream(File(SCHEMATIC_FOLDER, "id9.schematic"))

    val binaryTagDriver = MWork.getBinaryTagDriver()

    val gzipInputStream = GZIPInputStream(fileInputStream)
    val compoundBinaryTag = binaryTagDriver.readCompoundBinaryTag(ByteArrayInputStream(gzipInputStream.readAllBytes()))
    val width = compoundBinaryTag.getShort("Width")
    val length = compoundBinaryTag.getShort("Length")
    val height = compoundBinaryTag.getShort("Height")
    val blocks = compoundBinaryTag.getByteArray("Blocks")
    val blocksData = compoundBinaryTag.getByteArray("Data")

    for (x in 0 until width) {
        for (y in 0 until height) {
            for (z in 0 until length) {
                val index: Int = y * width * length + z * width + x
                val blockByIndex = blocks[index]
                val block = if (blockByIndex < 0) blockByIndex + 256 else blockByIndex.toInt()
                val blockData = blocksData[index]

                val materialKey: MaterialKey
                materialKey = if (block == 0) {
                    Material.AIR
                } else {
                    MaterialKey.from(block, blockData.toInt())
                }
                var reverse: Block?
                if (materialKey !== Material.AIR) {
                    reverse = blockTransformerController.reverse(materialKey)
         */
/*           if (reverse == null) {
                        reverse = blockTransformerController.reverse(MaterialKey.from(block))
                        if (reverse == null) {
                            println("null: $materialKey")
                        } else {
                            println("reverse: $materialKey -> $reverse")
                        }
                    }*//*

                }
            }

        }
    }
}*/
