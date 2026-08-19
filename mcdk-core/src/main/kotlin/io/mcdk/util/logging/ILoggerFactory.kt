package io.mcdk.util.logging

import kotlin.reflect.KClass

public interface ILoggerFactory {
    public fun getLogger(className: String, loggerName: String? = null): ILogger
    public fun getLogger(kClass: KClass<*>, loggerName: String? = null): ILogger =
        getLogger(kClass.qualifiedName ?: loggerName ?: "unknown", loggerName)

    public operator fun get(loggingLevel: ILogLevel): Boolean
    public operator fun set(loggingLevel: ILogLevel, enabled: Boolean?)
}

public inline fun <reified T> ILoggerFactory.getLogger(loggerName: String? = null): ILogger =
    getLogger(T::class, loggerName)
