package io.mcdk.api.text

import io.mcdk.util.logging.format.AnsiElement

public sealed interface TextColor : AnsiElement

public class RgbTextColor(public val rgbaColor: RgbaColor) : TextColor {
    override fun toAnsi(): String = "\u001B[38${rgbaColor.toAnsi()}"

    public fun toHexString(): String {
        val r = rgbaColor.red.toString(16).padStart(2, '0')
        val g = rgbaColor.green.toString(16).padStart(2, '0')
        val b = rgbaColor.blue.toString(16).padStart(2, '0')
        return "#$r$g$b"
    }
}

public enum class NamedTextColor(
    public val ansi: String,
    public val mcName: String
) : TextColor {
    BLACK("\u001B[0;30m", "black"),
    DARK_BLUE("\u001B[0;34m", "dark_blue"),
    DARK_GREEN("\u001B[0;32m", "dark_green"),
    DARK_AQUA("\u001B[0;36m", "dark_aqua"),
    DARK_RED("\u001B[0;31m", "dark_red"),
    DARK_PURPLE("\u001B[0;35m", "dark_purple"),
    GOLD("\u001B[0;33m", "gold"),
    GRAY("\u001B[0;37m", "gray"),
    DARK_GRAY("\u001B[0;90m", "dark_gray"),
    BLUE("\u001B[0;94m", "blue"),
    GREEN("\u001B[0;92m", "green"),
    AQUA("\u001B[0;96m", "aqua"),
    RED("\u001B[0;91m", "red"),
    LIGHT_PURPLE("\u001B[0;95m", "light_purple"),
    YELLOW("\u001B[0;93m", "yellow"),
    WHITE("\u001B[0;97m", "white");

    override fun toAnsi(): String = ansi

}
