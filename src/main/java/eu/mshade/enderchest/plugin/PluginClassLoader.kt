package eu.mshade.enderchest.plugin

import java.lang.reflect.Method
import java.net.URL
import java.net.URLClassLoader

object PluginClassLoader: URLClassLoader(arrayOf()) {

    fun add(url: URL) {
        super.addURL(url)
    }

}