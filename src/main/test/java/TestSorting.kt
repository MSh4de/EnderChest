import com.fasterxml.jackson.databind.ObjectMapper
import eu.mshade.enderchest.plugin.DefaultPluginManager
import eu.mshade.enderframe.plugin.Plugin
import eu.mshade.enderframe.plugin.PluginManifest

fun main() {
    val pluginManager = DefaultPluginManager(ObjectMapper())

    pluginManager.register(PluginManifest("TestPlugin", "1.0", "TestPlugin", "TestPlugin", listOf("MShade"), listOf("Plugin1")), TestPlugin())
    pluginManager.register(PluginManifest("Plugin1", "1.0", "Plugin1", "Plugin1", listOf("MShade"), listOf("Plugin3")), TestPlugin())
    pluginManager.register(PluginManifest("Plugin2", "1.0", "Plugin2", "Plugin2", listOf("MShade"), listOf("Plugin1")), TestPlugin())
    pluginManager.register(PluginManifest("Plugin3", "1.0", "Plugin3", "Plugin3", listOf("MShade")), TestPlugin())
    pluginManager.register(PluginManifest("Plugin4", "1.0", "Plugin3", "Plugin3", listOf("MShade"), listOf("A")), TestPlugin())

    //Plugin3,

    pluginManager.enablePlugins()

}


class TestPlugin: Plugin{
    override fun onEnable() {
        println("Enabled")
    }

    override fun onDisable() {
        println("Disabled")
    }

}

