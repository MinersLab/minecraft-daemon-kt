package io.mcdk.plugin.platform.vanilla.helper.text

import io.mcdk.api.text.AnnotatedText
import io.mcdk.api.text.ClickEvent
import io.mcdk.api.text.HoverEvent
import io.mcdk.api.text.NamedTextColor
import io.mcdk.api.text.RgbTextColor
import io.mcdk.api.text.ListText
import io.mcdk.api.text.Text
import io.mcdk.api.text.TextColor
import io.mcdk.util.platform.OnlyIn
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonArray

@OnlyIn("[26,)", "vanilla")
@Suppress("ClassName")
public open class TextSerializer_26_X : TextSerializer {

    protected open fun getColor(color: TextColor): String = when (color) {
        is NamedTextColor -> color.mcName
        is RgbTextColor -> color.toHexString()
    }

    protected open fun getHoverEvent(hoverEvent: HoverEvent): JsonObject? = when (hoverEvent) {
        is HoverEvent.ShowText -> buildJsonObject {
            put("action", JsonPrimitive("show_text"))
            put("value", toJson(hoverEvent.text))
        }
        is HoverEvent.Custom -> hoverEvent.json
        else -> null
    }

    protected open fun getClickEvent(clickEvent: ClickEvent): JsonObject? = when (clickEvent) {
        is ClickEvent.SuggestCommand -> buildJsonObject {
            put("action", JsonPrimitive("suggest_command"))
            put("command", JsonPrimitive(clickEvent.command))
        }
        is ClickEvent.Custom -> clickEvent.json
        else -> null
    }

    override fun toJson(text: Text): JsonObject =
        when (text) {
            is ListText -> JsonObject(
                mapOf(
                    "text" to JsonPrimitive(""),
                    "extra" to JsonArray(text.texts.map(::toJson))
                )
            )
            is AnnotatedText -> buildJsonObject {
                if (text.color != null) put("color", JsonPrimitive(getColor(text.color!!)))
                if (text.shadowColor != null) {
                    putJsonArray("shadow_color") {
                        add(JsonPrimitive(text.shadowColor!!.alpha / 255.0))
                        add(JsonPrimitive(text.shadowColor!!.red / 255.0))
                        add(JsonPrimitive(text.shadowColor!!.green / 255.0))
                        add(JsonPrimitive(text.shadowColor!!.blue / 255.0))
                    }
                }
                if (text.bold != null) put("bold", JsonPrimitive(text.bold))
                if (text.italic != null) put("italic", JsonPrimitive(text.italic))
                if (text.underlined != null) put("underlined", JsonPrimitive(text.underlined))
                if (text.strikeThrough != null) put("strikethrough", JsonPrimitive(text.strikeThrough))
                if (text.obfuscated != null) put("obfuscated", JsonPrimitive(text.obfuscated))
                if (text.insertion != null) put("insertion", JsonPrimitive(text.insertion))
                if (text.font != null) put("font", JsonPrimitive(text.font))
                if (text.hoverEvent != null) getHoverEvent(text.hoverEvent!!)?.let { put("hover_event", it) }
                if (text.clickEvent != null) getClickEvent(text.clickEvent!!)?.let { put("click_event", it) }
                for ((key, value) in toJson(text.text)) {
                    put(key, value)
                }
            }
            else -> JsonObject(
                mapOf("text" to JsonPrimitive(text.toPlainText()))
            )
        }

}
