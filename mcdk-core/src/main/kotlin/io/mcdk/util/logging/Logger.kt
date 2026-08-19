package io.mcdk.util.logging

import io.mcdk.util.logging.format.FormattedText
import io.mcdk.util.logging.format.blue
import io.mcdk.util.logging.format.bold
import io.mcdk.util.logging.format.cyan
import io.mcdk.util.logging.format.foregroundAnsi
import io.mcdk.util.logging.format.green
import io.mcdk.util.logging.format.reset
import io.mcdk.util.logging.format.yellow
import java.sql.Date
import java.text.SimpleDateFormat

public open class Logger(
    public val className: String,
    public val loggerName: String?,
    public val loggerFactory: ILoggerFactory
) : ILogger {

    private val loggingLevels: MutableMap<ILogLevel, Boolean> = mutableMapOf()
    override fun set(loggingLevel: ILogLevel, enabled: Boolean?) {
        if (enabled == null) {
            loggingLevels.remove(loggingLevel)
        } else {
            loggingLevels[loggingLevel] = enabled
        }
    }

    override fun get(loggingLevel: ILogLevel): Boolean = loggingLevels[loggingLevel] ?: loggerFactory[loggingLevel]

    override fun log(loggingLevel: ILogLevel, message: String, throwable: Throwable?) {
        if (!get(loggingLevel)) return
        val logText = FormattedText()
            .foregroundAnsi.cyan
            .append(SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date(System.currentTimeMillis())))
            .append(" ")
            .foregroundAnsi.green["[${Thread.currentThread().name}]"]
            .foregroundAnsi.blue[loggerName?.takeUnless(String::isBlank)?.let { " $it" } ?: ""][" "]
            .let(loggingLevel::format)[loggingLevel.name][" "]
            .bold.foregroundAnsi.blue[formattedName()]
            .reset[" - "]
            .bold.foregroundAnsi.yellow[message]
            .reset
        println(logText.toAnsi())
        throwable?.printStackTrace()
    }

    protected fun formattedName(length: Int = 36): String {
        if (className.length <= length) return className
        val parts = className.split(".")
        if (parts.size == 1) return className.takeLast(length)
        val abbreviated = StringBuilder()
        for (i in 0 until parts.size - 1) {
            abbreviated.append(parts[i].first())
            abbreviated.append('.')
        }
        abbreviated.append(parts.last())
        val result = abbreviated.toString()
        return if (result.length <= length) result else result.takeLast(length)
    }

}

