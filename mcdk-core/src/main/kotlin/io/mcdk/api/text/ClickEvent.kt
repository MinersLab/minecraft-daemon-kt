package io.mcdk.api.text

import kotlinx.serialization.json.JsonObject

public abstract class ClickEvent {

    public class Custom(public val json: JsonObject) : ClickEvent()

    public class SuggestCommand(
        public val command: String
    ) : ClickEvent()

}