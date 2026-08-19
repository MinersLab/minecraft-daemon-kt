package io.mcdk.plugin

import io.mcdk.Mcdk
import java.io.File
import java.net.URLClassLoader
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor

/**
 * 插件类加载器
 */
public class PluginClassLoader(
    public val mcdk: Mcdk,
    public val file: File,
    public val metadata: PluginMetadata
) : URLClassLoader(arrayOf(file.toURI().toURL())) {

    /**
     * 插件上下文
     */
    public val pluginLoadingContext: PluginLoadingContext = PluginLoadingContext(this)

    /**
     * 插件实例
     */
    public var instance: PluginInitializer? = null

    /**
     * 插件加载状态
     */
    public var status: PluginStatus = PluginStatus.IDLE

    public fun construct() {
        try {
            status = PluginStatus.LOADING
            val entrypoint = loadClass(metadata.entrypoint)?.kotlin
                ?: throw IllegalStateException("Unable to find entrypoint '${metadata.entrypoint}' of plugin '${metadata.name.value}'")
            if (!entrypoint.isSubclassOf(PluginInitializer::class)) throw IllegalStateException("Not a plugin class: ${metadata.entrypoint}")
            val newInstance = if (entrypoint.objectInstance != null) entrypoint.objectInstance
            else entrypoint.primaryConstructor?.call()
                ?: throw UnsupportedOperationException("Unable to construct plugin class '${metadata.entrypoint}'")
            instance = newInstance as PluginInitializer
        } catch (error: Throwable) {
            status = PluginStatus.ERROR
            throw error
        }
    }

    public fun load() {
        instance?.onLoad()
        status = PluginStatus.LOADED
    }

    override fun close() {
        instance?.onDispose()
        pluginLoadingContext.close()
        status = PluginStatus.CLOSED
        super.close()
    }

}