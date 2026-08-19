package io.mcdk.util.logging

import io.mcdk.util.logging.format.FormattedText
import io.mcdk.util.logging.format.brightBlack
import io.mcdk.util.logging.format.foregroundAnsi

public interface ILogLevel {
    public val name: String
    public fun format(formattedText: FormattedText): FormattedText = formattedText.foregroundAnsi.brightBlack
}
