package io.mcdk.plugin.platform.vanilla.helper.text

import io.mcdk.api.text.Text
import kotlinx.serialization.json.JsonElement

public sealed interface TextSerializer {

    public fun toJson(text: Text): JsonElement

}
