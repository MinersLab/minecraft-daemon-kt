package io.mcdk.plugin

import io.mcdk.Mcdk
import io.mcdk.core.Identifier
import io.mcdk.util.configuration.ConfigTypes
import io.mcdk.util.platform.VersionRange
import java.io.File
import java.util.zip.ZipFile

public class PluginManager(private val mcdk: Mcdk) {

    public val pluginDirectory: File = mcdk.runtimeDirectory.resolve("plugins").also(File::mkdirs)

    public val plugins: Map<Identifier.Namespace, PluginClassLoader>
        field = mutableMapOf()

    public fun scan(): Iterator<Pair<File, PluginMetadata>> = iterator {
        for (pluginFile in pluginDirectory.walk()) {
            if (!pluginFile.name.endsWith(".jar")) continue
            yield(pluginFile to readMetadata(pluginFile))
        }
    }

    private fun readMetadata(file: File): PluginMetadata =
        ZipFile(file).use { zipFile ->
            zipFile.getInputStream(zipFile.getEntry("META-INF/plugin.jsonc")).use { inputStream ->
                inputStream.use { stream ->
                    ConfigTypes.Jsonc.decodeFromStream(stream, PluginMetadata.serializer())
                }
            }
        }

    public fun reload(name: Identifier.Namespace): Unit =
        reload(plugins[name]?.file ?: throw IllegalStateException("Plugin not found: $name"))

    public fun reload(file: File) {
        val plugin = getPluginByFile(file) ?: throw IllegalStateException("Plugin not found: $file")
        dispose(plugin.key)
        val meta = readMetadata(file)
        add(file, meta)
        val loader = plugins[meta.name] ?: return
        construct(loader)
        load(loader)
    }

    public fun add(file: File, metadata: PluginMetadata = readMetadata(file)) {
        val name = metadata.name
        if (name in plugins) dispose(name)
        val loader = PluginClassLoader(mcdk, file, metadata)
        plugins[name] = loader
    }

    public fun dispose(name: Identifier.Namespace) {
        plugins[name]?.close()
        plugins.remove(name)
    }

    public fun construct() {
        plugins.values.forEach(::construct)
    }

    public fun load(plugin: PluginClassLoader) {
        if (plugin.status == PluginStatus.LOADED) return
        for (dependency in plugin.metadata.dependencies) {
            val plugin = getPluginByName(dependency.name)
                ?: throw IllegalStateException("Expected dependency '${dependency}', but found NOTHING")
            require(plugin.value.metadata.version in (dependency.versionRange ?: VersionRange.ALL)) {
                "Expected dependency '$dependency', but found ${plugin.value.metadata.version}"
            }
            load(plugin.value)
        }
        plugin.pluginLoadingContext.withContext {
            plugin.load()
        }
    }

    public fun construct(plugin: PluginClassLoader) {
        if (plugin.status != PluginStatus.IDLE) return
        for (dependency in plugin.metadata.dependencies) {
            val plugin = getPluginByName(dependency.name)
                ?: throw IllegalStateException("Expected dependency '${dependency}', but found NOTHING")
            require(plugin.value.metadata.version in (dependency.versionRange ?: VersionRange.ALL)) {
                "Expected dependency '$dependency', but found ${plugin.value.metadata.version}"
            }
            construct(plugin.value)
        }
        plugin.pluginLoadingContext.withContext {
            plugin.construct()
        }
    }

    public fun load() {
        plugins.values.forEach(::load)
    }

    public fun dispose() {
        plugins.keys.forEach {
            runCatching {
                dispose(it)
            }
        }
    }

    public fun getPluginByFile(file: File): Map.Entry<Identifier.Namespace, PluginClassLoader>? =
        plugins.entries.firstOrNull { it.value.file == file }

    public fun getPluginByName(name: Identifier.Namespace): Map.Entry<Identifier.Namespace, PluginClassLoader>? =
        plugins[name]?.file?.let { getPluginByFile(it) }

    public fun getPluginMetadata(plugin: PluginInitializer): PluginMetadata = plugins.entries.firstOrNull { it.value.instance == plugin }?.value?.metadata
        ?: throw IllegalStateException("Plugin not found: ${plugin::class.simpleName} ($plugin))")

}