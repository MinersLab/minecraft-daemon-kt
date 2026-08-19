package io.mcdk.util.logging

public interface ILogger {

    public fun log(loggingLevel: ILogLevel, message: String, throwable: Throwable? = null)

    public fun info(message: String, throwable: Throwable? = null): Unit = log(LogLevel.INFO, message, throwable)
    public fun warn(message: String, throwable: Throwable? = null): Unit = log(LogLevel.WARN, message, throwable)
    public fun error(message: String, throwable: Throwable? = null): Unit = log(LogLevel.ERROR, message, throwable)
    public fun debug(message: String, throwable: Throwable? = null): Unit = log(LogLevel.DEBUG, message, throwable)
    public fun trace(message: String, throwable: Throwable? = null): Unit = log(LogLevel.TRACE, message, throwable)
    public fun fatal(message: String, throwable: Throwable? = null): Unit = log(LogLevel.FATAL, message, throwable)

    public operator fun get(loggingLevel: ILogLevel): Boolean
    public operator fun set(loggingLevel: ILogLevel, enabled: Boolean?)

}
