package io.mcdk.plugin

public class PluginLoadingContext(
    public val classLoader: PluginClassLoader
) {

    public val pluginLifecycleListeners: MutableSet<PluginLifecycleListener> = mutableSetOf()

    public companion object {
        @JvmField
        public val SCOPED_VALUE: ScopedValue<PluginLoadingContext> = ScopedValue.newInstance()

        public val current: PluginLoadingContext
            get() = SCOPED_VALUE.get() ?: Thread.currentThread().contextClassLoader.takeIf(PluginClassLoader::class.java::isInstance)?.let {
                PluginLoadingContext(it as PluginClassLoader)
            } ?: throw IllegalStateException("No PluginLoadingContext found in the current thread")
    }

    public fun close() {
        pluginLifecycleListeners.forEach(PluginLifecycleListener::onDispose)
        pluginLifecycleListeners.clear()
    }

}

public fun <T> PluginLoadingContext.withContext(block: () -> T): T {
    val previousClassLoader = Thread.currentThread().contextClassLoader
    Thread.currentThread().contextClassLoader = classLoader
    return try {
        ScopedValue.where(PluginLoadingContext.SCOPED_VALUE, this).call(
            ScopedValue.CallableOp { block() }
        )
    } finally {
        Thread.currentThread().contextClassLoader = previousClassLoader
    }
}
