package io.mcdk.util.logging.format

public interface AnsiElement {
    public fun toAnsi(): String
}

public data class PlainAnsiElement(public val text: String) : AnsiElement {
    override fun toAnsi(): String = text
}
