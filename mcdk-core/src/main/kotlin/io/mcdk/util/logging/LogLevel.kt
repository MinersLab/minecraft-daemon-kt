package io.mcdk.util.logging

import io.mcdk.util.logging.format.FormattedText
import io.mcdk.util.logging.format.brightBlack
import io.mcdk.util.logging.format.brightRed
import io.mcdk.util.logging.format.foregroundAnsi
import io.mcdk.util.logging.format.magenta
import io.mcdk.util.logging.format.red
import io.mcdk.util.logging.format.yellow

public enum class LogLevel : ILogLevel {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    FATAL;

    override fun format(formattedText: FormattedText): FormattedText = when (this) {
        INFO -> formattedText.foregroundAnsi
        WARN -> formattedText.foregroundAnsi.yellow
        ERROR -> formattedText.foregroundAnsi.brightRed
        DEBUG -> formattedText.foregroundAnsi.magenta
        TRACE -> formattedText.foregroundAnsi.brightBlack
        FATAL -> formattedText.foregroundAnsi.red
    }

}
