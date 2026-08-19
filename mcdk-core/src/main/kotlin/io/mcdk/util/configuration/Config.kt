package io.mcdk.util.configuration

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import io.mcdk.core.Identifier
import io.mcdk.util.Ref
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.KSerializer

public class Config<T : Any>(
    public val name: Identifier,
    public val configType: ConfigType,
    public val configContainer: ConfigContainer,
    public val serializer: KSerializer<T>,
    public val autoSavable: Boolean = true,
    public val saveWhenChanged: Boolean = true,
    public val initializer: ((config: Config<T>) -> Unit)? = null
) : Ref<T> {

    public val stateFlow: StateFlow<T?> = MutableStateFlow(null)

    private var value: Option<T> = None

    public fun save() {
        configContainer.createConfig()
        configContainer.write {
            configType.encodeToStream(
                get(),
                this,
                serializer
            )
        }
    }

    override fun clear() {
        value = None
        configContainer.deleteConfig()
        (stateFlow as MutableStateFlow).tryEmit(null)
    }

    override fun get(): T = when (val data = value) {
        is Some -> data.value
        is None -> {
            if (!configContainer.isInitialized) {
                if (initializer != null) {
                    initializer(this)
                } else {
                    throw IllegalStateException("Config $name is not found and no initializer is provided")
                }
            }
            val loadedValue = configContainer.read {
                configType.decodeFromStream(
                    this,
                    serializer
                )
            }
            (stateFlow as MutableStateFlow).tryEmit(loadedValue)
            value = Some(loadedValue)
            loadedValue
        }
    }

    override fun hasValue(): Boolean = value is Some || configContainer.isInitialized

    override fun set(value: T) {
        this.value = Some(value)
        (stateFlow as MutableStateFlow).tryEmit(value)
        if (autoSavable) {
            save()
        }
    }

}
