package io.mcdk.api.text

import io.mcdk.util.logging.format.AnsiElement
import io.mcdk.util.logging.format.FormattedText
import io.mcdk.util.logging.format.bold
import io.mcdk.util.logging.format.italic
import io.mcdk.util.logging.format.reset
import io.mcdk.util.logging.format.strikeThrough
import io.mcdk.util.logging.format.underlined

public sealed interface Text : AnsiElement {
    public fun toPlainText(): String
}

public data class PlainText(
    public val text: String = ""
) : Text {
    override fun toPlainText(): String = text
    override fun toAnsi(): String = text
}

public data class ListText(
    public val texts: List<Text> = emptyList()
) : Text {
    public constructor(vararg texts: Text) : this(texts.toList())

    override fun toPlainText(): String = texts.joinToString("") { it.toPlainText() }
    override fun toAnsi(): String = texts.joinToString("") { it.toAnsi() }
}

public data class AnnotatedText(
    public val text: Text,
    public val color: TextColor? = null,
    public val shadowColor: RgbaColor? = null,
    public val bold: Boolean? = null,
    public val italic: Boolean? = null,
    public val underlined: Boolean? = null,
    public val strikeThrough: Boolean? = null,
    public val obfuscated: Boolean? = null,
    public val insertion: String? = null,
    public val font: String? = null,
    public val hoverEvent: HoverEvent? = null,
    public val clickEvent: ClickEvent? = null
) : Text {
    override fun toPlainText(): String = text.toPlainText()
    override fun toAnsi(): String {
        var formattedText = FormattedText()
        if (color != null) formattedText = formattedText.append(color)
        if (bold == true) formattedText = formattedText.bold
        if (italic == true) formattedText = formattedText.italic
        if (underlined == true) formattedText = formattedText.underlined
        if (strikeThrough == true) formattedText = formattedText.strikeThrough
        formattedText = formattedText.append(text.toAnsi())
        if (color != null) {
            formattedText = formattedText.reset
        }
        return formattedText.toAnsi()
    }
}
