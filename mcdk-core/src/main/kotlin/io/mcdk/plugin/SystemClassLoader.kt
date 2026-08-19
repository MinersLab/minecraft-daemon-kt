package io.mcdk.plugin

import io.mcdk.Mcdk

public class SystemClassLoader(
    public val mcdk: Mcdk,
    parent: ClassLoader = Thread.currentThread().contextClassLoader
) : ClassLoader(parent) {

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String): Class<*> {
        val classLoaders = mcdk.pluginManager.plugins.values + parent
        for (classLoader in classLoaders) {
            try {
                return classLoader.loadClass(name)
            } catch (_: ClassNotFoundException) { }
        }
        return super.loadClass(name)
    }

}