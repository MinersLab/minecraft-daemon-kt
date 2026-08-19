package io.mcdk.util.logging.format

import io.mcdk.api.text.RgbaColor
import kotlin.collections.plus

public data class FormattedText(public val elements: List<AnsiElement> = listOf()) : AnsiElement {
    public fun styled(style: AnsiStyle): FormattedText = copy(elements = elements + style)
    public fun colored(color: RgbaColor): FormattedText = copy(elements = elements + color)
    public fun colored(color: AnsiColor): FormattedText = copy(elements = elements + color)
    public fun append(text: String): FormattedText = copy(elements = elements + PlainAnsiElement(text))
    public fun append(element: AnsiElement): FormattedText = copy(elements = elements + element)

    override fun toAnsi(): String = elements.joinToString(separator = "") { it.toAnsi() }

    public operator fun get(vararg texts: String): FormattedText = texts.fold(this) { acc, text -> acc.append(text) }
    public operator fun get(vararg elements: AnsiElement): FormattedText =
        elements.fold(this) { acc, element -> acc.append(element) }

    public operator fun plus(text: String): FormattedText = append(text)
    public operator fun plus(element: AnsiElement): FormattedText = append(element)
}

public val FormattedText.a: FormattedText get() = copy()

public val FormattedText.foregroundAnsi: FormattedText get() = copy(elements = elements + AnsiStyle.FOREGROUND_ANSI)
public val FormattedText.backgroundAnsi: FormattedText get() = copy(elements = elements + AnsiStyle.BACKGROUND_ANSI)
public val FormattedText.backgroundRgb: FormattedText get() = copy(elements = elements + AnsiStyle.BACKGROUND_RGB)
public val FormattedText.foregroundRgb: FormattedText get() = copy(elements = elements + AnsiStyle.FOREGROUND_RGB)

public val FormattedText.reset: FormattedText get() = copy(elements = elements + AnsiStyle.RESET)
public val FormattedText.bold: FormattedText get() = styled(AnsiStyle.BOLD)
public val FormattedText.dimmed: FormattedText get() = styled(AnsiStyle.DIM)
public val FormattedText.italic: FormattedText get() = styled(AnsiStyle.ITALIC)
public val FormattedText.underlined: FormattedText get() = styled(AnsiStyle.UNDERLINE)
public val FormattedText.blinked: FormattedText get() = styled(AnsiStyle.BLINK)
public val FormattedText.strikeThrough: FormattedText get() = styled(AnsiStyle.STRIKE_THROUGH)
public val FormattedText.reversed: FormattedText get() = styled(AnsiStyle.REVERSE)
public val FormattedText.hidden: FormattedText get() = styled(AnsiStyle.HIDDEN)

public val FormattedText.black: FormattedText get() = colored(AnsiColor.BLACK)
public val FormattedText.red: FormattedText get() = colored(AnsiColor.RED)
public val FormattedText.green: FormattedText get() = colored(AnsiColor.GREEN)
public val FormattedText.yellow: FormattedText get() = colored(AnsiColor.YELLOW)
public val FormattedText.blue: FormattedText get() = colored(AnsiColor.BLUE)
public val FormattedText.magenta: FormattedText get() = colored(AnsiColor.MAGENTA)
public val FormattedText.cyan: FormattedText get() = colored(AnsiColor.CYAN)
public val FormattedText.white: FormattedText get() = colored(AnsiColor.WHITE)
public val FormattedText.brightBlack: FormattedText get() = colored(AnsiColor.BRIGHT_BLACK)
public val FormattedText.brightRed: FormattedText get() = colored(AnsiColor.BRIGHT_RED)
public val FormattedText.brightGreen: FormattedText get() = colored(AnsiColor.BRIGHT_GREEN)
public val FormattedText.brightYellow: FormattedText get() = colored(AnsiColor.BRIGHT_YELLOW)
public val FormattedText.brightBlue: FormattedText get() = colored(AnsiColor.BRIGHT_BLUE)
public val FormattedText.brightMagenta: FormattedText get() = colored(AnsiColor.BRIGHT_MAGENTA)
public val FormattedText.brightCyan: FormattedText get() = colored(AnsiColor.BRIGHT_CYAN)
public val FormattedText.brightWhite: FormattedText get() = colored(AnsiColor.BRIGHT_WHITE)
