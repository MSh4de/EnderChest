package eu.mshade.enderchest.plugin

import java.net.URL
import java.net.URLClassLoader

object PluginClassLoader : URLClassLoader(arrayOf()) {

    fun add(url: URL) {
        super.addURL(url)
    }

}


class NewPluginClassLoader(vararg urls: URL) : URLClassLoader(urls) {

    companion object {
        private val classes = mutableMapOf<String, Class<*>>()
    }

/*    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String): Class<*>? {

    }*/

    override fun findClass(name: String): Class<*>? {
        var clazz = classes[name]

        if (clazz == null) {
            clazz = super.findClass(name)
            classes[name] = clazz
        }

        return clazz
    }
}