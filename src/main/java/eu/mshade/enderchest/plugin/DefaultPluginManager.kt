package eu.mshade.enderchest.plugin

import com.fasterxml.jackson.databind.ObjectMapper
import eu.mshade.enderframe.plugin.Plugin
import eu.mshade.enderframe.plugin.PluginManager
import eu.mshade.enderframe.plugin.PluginManifest
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.net.URLClassLoader
import java.nio.file.Path
import java.util.jar.JarFile
import kotlin.system.exitProcess

class DefaultPluginManager(val objectMapper: ObjectMapper): PluginManager {

    companion object{
        val LOGGER = LoggerFactory.getLogger(DefaultPluginManager::class.java)
    }

    val plugins = mutableMapOf<String, Plugin>()
    val pluginManifests = mutableMapOf<String, PluginManifest>()
    val pluginStates = mutableMapOf<String, Boolean>()


    fun register(pluginManifest: PluginManifest, plugin: Plugin) {
        plugins[pluginManifest.name] = plugin
        pluginManifests[pluginManifest.name] = pluginManifest
        pluginStates[pluginManifest.name] = false
    }

    override fun loadPlugin(path: Path): Plugin {
        TODO("Not yet implemented")
    }

    override fun loadPlugins(directory: Path) {

        directory.toFile().mkdirs()

        directory.toFile().listFiles()?.forEach {
            if(it.extension == "jar"){
                val jarFile = JarFile(it)
                val jarEntry = jarFile.getJarEntry("plugin.json")
                val inputStream = jarFile.getInputStream(jarEntry)
                val pluginManifest = getPluginManifest(inputStream)
                val urlClassLoader = URLClassLoader(arrayOf(it.toURI().toURL()))
                val pluginClass = urlClassLoader.loadClass(pluginManifest.main)

                val plugin = pluginClass.getDeclaredConstructor().newInstance() as Plugin

                register(pluginManifest, plugin)

                LOGGER.info("Loaded plugin ${pluginManifest.name} version ${pluginManifest.version} by ${pluginManifest.authors.joinToString(", ")}")
            }
        }

    }

    override fun enablePlugin(plugin: Plugin) {
        TODO("Not yet implemented")
    }

    override fun enablePlugins() {
        var idx = 0

        //copy pluginStates
        val cpyPluginStates = pluginStates.toMutableMap()

        for ((pluginName, state) in pluginStates) {

            val pluginManifest = pluginManifests[pluginName]
            if (pluginManifest == null) {
                cpyPluginStates.remove(pluginName)
                LOGGER.error("Plugin $pluginName not found in pluginManifests")
                continue
            }

            for (denpendency in pluginManifest.depends) {
                if (pluginStates[denpendency] == null) {
                    cpyPluginStates.remove(pluginName)
                    LOGGER.error("Plugin $pluginName depends on $denpendency but it is not found in plugins")
                    continue
                }
            }
        }

/*        println(cpyPluginStates)
        exitProcess(0)*/

        var pluginName = poolPlugin(cpyPluginStates, idx)
        while (pluginName != null) {
            val plugin = plugins[pluginName]!!
            if (checkDependencies(pluginManifests[pluginName]!!)) {
                plugin.onEnable()
                cpyPluginStates[pluginName] = true
                pluginStates[pluginName] = true
            }
            pluginName = poolPlugin(cpyPluginStates, idx)
            idx++
        }

    }

    fun poolPlugin(pluginStates: MutableMap<String, Boolean>, idx: Int): String? {
        val filter = pluginStates.filter { !it.value }
        if (filter.isEmpty()) {
            return null
        }

        return filter.keys.toList()[idx % filter.size]
    }

    fun checkDependencies(pluginManifest: PluginManifest): Boolean {
        for (depend in pluginManifest.depends) {
            if (pluginStates[depend] != true) {
                return false
            }
        }
        return true
    }

    override fun disablePlugin(plugin: Plugin) {
        TODO("Not yet implemented")
    }

    override fun getPluginManifest(inputStream: InputStream): PluginManifest {
        val pluginTree = objectMapper.readTree(inputStream)
        val name = pluginTree.get("name").asText()
        val version = pluginTree.get("version").asText()
        val main = pluginTree.get("main").asText()
        val description = pluginTree.get("description")?.asText()?: ""
        val authors = pluginTree.get("authors")?.map { it.asText() }?: listOf()
        val depends = pluginTree.get("depends")?.map { it.asText() }?: listOf()

        return PluginManifest(name, version, main, description, authors, depends)
    }
}