package io.mcdk.api.text

@Suppress("FunctionName")
public fun RainbowText(plainText: String): ListText = ListText(
    buildList {
        for ((index, char) in plainText.withIndex()) {
            val hue = index.toFloat() / plainText.length
            val rgb = RgbaColor.fromHsv(hue, 1f, 1f)
            val annotatedChar = AnnotatedText(PlainText(char.toString()), color = RgbTextColor(rgb))
            add(annotatedChar)
        }
    }
)