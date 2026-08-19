package io.mcdk.api.text

import io.mcdk.util.logging.format.AnsiElement
import kotlin.math.abs

public data class RgbaColor(
    public val red: Int,
    public val green: Int,
    public val blue: Int,
    public val alpha: Int = 255
) : AnsiElement {
    init {
        require(red in 0..255) { "Red value must be between 0 and 255" }
        require(green in 0..255) { "Green value must be between 0 and 255" }
        require(blue in 0..255) { "Blue value must be between 0 and 255" }
        require(alpha in 0..255) { "Alpha value must be between 0 and 255" }
    }

    public companion object {

        public fun fromHsv(hue: Float, saturation: Float, value: Float): RgbaColor {
            val c = value * saturation
            val x = c * (1 - abs((hue * 6) % 2 - 1))
            val m = value - c

            val (r, g, b) = when {
                hue < 1f / 6f -> Triple(c, x, 0f)
                hue < 2f / 6f -> Triple(x, c, 0f)
                hue < 3f / 6f -> Triple(0f, c, x)
                hue < 4f / 6f -> Triple(0f, x, c)
                hue < 5f / 6f -> Triple(x, 0f, c)
                else -> Triple(c, 0f, x)
            }

            return RgbaColor(
                ((r + m) * 255).toInt(),
                ((g + m) * 255).toInt(),
                ((b + m) * 255).toInt()
            )
        }

    }

    override fun toAnsi(): String = ";2;${red};${green};${blue}m"


}