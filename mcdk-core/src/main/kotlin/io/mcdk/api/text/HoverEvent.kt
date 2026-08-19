package io.mcdk.api.text

import kotlinx.serialization.json.JsonObject

public abstract class HoverEvent {

    public class ShowText(
        public val text: Text
    ) : HoverEvent()

    public class Custom(public val json: JsonObject) : HoverEvent()

}
