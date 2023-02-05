package virtualworld

import eu.mshade.mwork.binarytag.BinaryTagDriver
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import eu.mshade.mwork.binarytag.entity.ListBinaryTag
import java.io.File

fun main() {
    val file = File("testChunk.dat")
    val binaryTagDriver = BinaryTagDriver()
    val readCompoundBinaryTag = binaryTagDriver.readCompoundBinaryTag(file)
    println(((readCompoundBinaryTag.getBinaryTag("sections") as ListBinaryTag).size()))
}