package virtualworld

import eu.mshade.mwork.binarytag.BinaryTagDriver
import eu.mshade.mwork.binarytag.entity.ListBinaryTag
import eu.mshade.mwork.binarytag.segment.SegmentBinaryTag
import java.io.File

fun main() {
    val binaryTagDriver = BinaryTagDriver()
    val virtualWorldFolder = File(System.getProperty("user.dir"), "worlds/world")
    virtualWorldFolder.mkdirs()

    val segmentBinaryTag = SegmentBinaryTag(
        File(virtualWorldFolder, "/indices/" + regionID(7, 7) + ".dat"),
        File(virtualWorldFolder, "/regions/"+ regionID(7, 7) + ".dat"),
        binaryTagDriver
    )

    segmentBinaryTag.compoundSectionIndex.getSectionIndicesByName().forEach{(name, sectionIndex) ->
        println("name: $name, size: ${sectionIndex[0].size}")
    }
    println("size: ${(segmentBinaryTag.readCompoundBinaryTag("0,0")!!.getBinaryTag("sections")!! as ListBinaryTag).size()}")




/*    val metadataKeyValueBinaryTagMarshal = MetadataKeyValueBinaryTagMarshal(binaryTagDriver)


    val chunkSafeguard = ChunkSafeguard()
    val defaultWorld = DefaultWorld(chunkSafeguard, File("test"))

    val deserialize = ChunkBinaryTagMarshal.deserialize(
        segmentBinaryTag.readCompoundBinaryTag("0,0")!!,
        defaultWorld,
        metadataKeyValueBinaryTagMarshal
    )

    val serialize = ChunkBinaryTagMarshal.serialize(deserialize, metadataKeyValueBinaryTagMarshal)
    val byteArrayDataOutputStream = ByteArrayOutputStream()
    binaryTagDriver.writeCompoundBinaryTag(serialize, File("testChunk.dat"))

    println(byteArrayDataOutputStream.toByteArray().contentToString())*/
}

fun regionID(chunkX: Int, chunkZ: Int): String {
    return "${(chunkX shr 5)},${(chunkZ shr 5)}"
}