package io.mcdk.plugin

import io.mcdk.api.event.IEvent
import io.mcdk.core.Identifier

private class DelegatedEvent<T, R>(
    private val pluginLoadingContext: PluginLoadingContext,
    private val event: IEvent<T, R>,
) : IEvent<T, R> by event, PluginLifecycleListener {

    init {
        pluginLoadingContext.pluginLifecycleListeners.add(this)
    }

    private val listeners: MutableMap<Identifier, MutableList<(T) -> R>> = mutableMapOf()

    override fun register(phase: Identifier, listener: (T) -> R): (T) -> R {
        val wrappedListener: (T) -> R = {
            pluginLoadingContext.withContext { listener(it) }
        }
        listeners.getOrPut(phase, ::mutableListOf).add(wrappedListener)
        return event.register(phase, wrappedListener)
    }

    override fun unregister(listener: (T) -> R, phase: Identifier) {
        listeners[phase]?.remove(listener)
        event.unregister(listener, phase)
    }

    override fun onDispose() {
        listeners.forEach { (phase, phaseListeners) ->
            phaseListeners.forEach { listener ->
                event.unregister(listener, phase)
            }
        }
        listeners.clear()
    }

}

/**
 * 返回一个委托的事件对象，该对象在调用监听器时会使用当前插件的类加载器上下文。
 * 这对于确保在插件生命周期内正确加载类和资源非常重要。
 * 该委托事件对象会在插件卸载时自动注销所有注册的监听器，以防止内存泄漏。
 */
public val <T, R> IEvent<T, R>.delegated: IEvent<T, R>
    get() = DelegatedEvent(PluginLoadingContext.current, this)
