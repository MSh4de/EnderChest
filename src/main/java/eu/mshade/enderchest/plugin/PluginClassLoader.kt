package eu.mshade.enderchest.plugin

import java.net.URL
import java.net.URLClassLoader

class PluginClassLoader(vararg urls: URL) : URLClassLoader(urls) {

    companion object {
        private val classes = mutableMapOf<String, Class<*>>()
    }

    override fun findClass(name: String): Class<*>? {
        var clazz = classes[name]

        if (clazz == null) {
            clazz = super.findClass(name)
            classes[name] = clazz
        }

        return clazz
    }
}