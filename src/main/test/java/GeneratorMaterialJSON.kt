import com.fasterxml.jackson.databind.ObjectMapper
import eu.mshade.enderframe.item.Material
import java.io.File

fun main() {
    val file = File("materials.json")

    val objectMapper = ObjectMapper()
    val jsonNode = objectMapper.createObjectNode()


    var id = 0
    Material.getRegisteredNamespacedKeys().forEach{
        jsonNode.put(it.key.lowercase(), id++)
    }

    objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, jsonNode)

}