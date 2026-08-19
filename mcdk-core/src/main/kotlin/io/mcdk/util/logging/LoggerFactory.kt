package io.mcdk.util.logging


public object LoggerFactory : ILoggerFactory {

    private val logLevelSettings: MutableMap<ILogLevel, Boolean> = mutableMapOf()
    public override operator fun get(loggingLevel: ILogLevel): Boolean = logLevelSettings[loggingLevel] ?: true
    public override operator fun set(loggingLevel: ILogLevel, enabled: Boolean?) {
        if (enabled == null) {
            logLevelSettings.remove(loggingLevel)
        } else {
            logLevelSettings[loggingLevel] = enabled
        }
    }

    override fun getLogger(className: String, loggerName: String?): Logger {
        return Logger(className, loggerName, this)
    }

}
