package io.mcdk.util.configuration

import io.mcdk.Mcdk
import io.mcdk.core.Identifier
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import java.io.File
import kotlin.reflect.KProperty0
import kotlin.reflect.KVisibility
import kotlin.reflect.jvm.isAccessible

public class ConfigManager(mcdk: Mcdk) {

    public val configDirectory: File = mcdk.runtimeDirectory.resolve("config").also(File::mkdirs)
    private val entries: MutableMap<Identifier, Config<*>> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    public fun <T : Any> getConfig(
        name: Identifier,
        serializer: KSerializer<T>,
        configType: ConfigType = ConfigTypes.Jsonc,
        autoSavable: Boolean = true,
        saveWhenChanged: Boolean = true,
        configContainer: ConfigContainer = FileSystemConfigContainer(this, name, configType),
        initializer: ((config: Config<T>) -> Unit)? = null
    ): Config<T> {
        if (!entries.containsKey(name)) {
            val config =
                Config(name, configType, configContainer, serializer, autoSavable, saveWhenChanged, initializer)
            entries[name] = config
        }
        return entries[name]!! as Config<T>
    }

    public fun saveAll() {
        entries.values.forEach {
            if (!it.autoSavable) return@forEach
            it.save()
        }
    }

    public inline fun <reified T : Any> getConfig(
        name: Identifier,
        configType: ConfigType = ConfigTypes.Jsonc,
        serializer: KSerializer<T> = serializer<T>(),
        autoSavable: Boolean = true,
        saveWhenChanged: Boolean = true,
        configContainer: ConfigContainer = FileSystemConfigContainer(this, name, configType),
        noinline initializer: ((config: Config<T>) -> Unit)? = defaults()
    ): Config<T> {
        return getConfig(name, serializer, configType, autoSavable, saveWhenChanged, configContainer, initializer)
    }

}

@Suppress("UNCHECKED_CAST")
public fun <T : Any> configured(property: KProperty0<T>): Config<T> {
    property.isAccessible = true
    val delegated = property.getDelegate()
    if (delegated !is Config<*>) {
        throw IllegalArgumentException("Property ${property.name} is not delegated to a Config")
    }
    return delegated as Config<T>
}

public inline fun <reified T : Any> defaults(): (config: Config<T>) -> Unit = { config ->
    val kClass = T::class
    val defaultConstructors = kClass.constructors.filter {
        it.parameters.all { parameter -> parameter.isOptional }
    }
    if (defaultConstructors.isEmpty()) {
        throw IllegalArgumentException("Class ${T::class} must have a default constructor")
    }
    val publicConstructor = defaultConstructors.firstOrNull { (it.visibility ?: KVisibility.PUBLIC) == KVisibility.PUBLIC } ?: throw IllegalStateException(
        "Default constructor of class ${T::class} must be public"
    )
    publicConstructor.isAccessible = true
    config.set(publicConstructor.callBy(emptyMap()))
    if (!config.saveWhenChanged) {
        config.save()
    }
}
