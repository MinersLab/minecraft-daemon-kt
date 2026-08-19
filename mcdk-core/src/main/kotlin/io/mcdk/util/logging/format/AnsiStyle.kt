package io.mcdk.util.logging.format

public enum class AnsiStyle : AnsiElement {
    BACKGROUND_ANSI, FOREGROUND_ANSI,
    BACKGROUND_RGB, FOREGROUND_RGB,
    RESET, BOLD, DIM, STRIKE_THROUGH, ITALIC, UNDERLINE, BLINK, REVERSE, HIDDEN;

    public override fun toAnsi(): String = when (this) {
        BACKGROUND_ANSI -> "\u001B[49m"
        FOREGROUND_ANSI -> "\u001B[39m"
        BACKGROUND_RGB -> "\u001B[48"
        FOREGROUND_RGB -> "\u001B[38"
        RESET -> "\u001B[0m"
        BOLD -> "\u001B[1m"
        DIM -> "\u001B[2m"
        STRIKE_THROUGH -> "\u001B[9m"
        ITALIC -> "\u001B[3m"
        UNDERLINE -> "\u001B[4m"
        BLINK -> "\u001B[5m"
        REVERSE -> "\u001B[7m"
        HIDDEN -> "\u001B[8m"
    }
}
