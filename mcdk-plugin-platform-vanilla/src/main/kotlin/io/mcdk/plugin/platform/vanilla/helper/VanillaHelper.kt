package io.mcdk.plugin.platform.vanilla.helper

public open class VanillaHelper {

    public companion object {
        @JvmField
        public val COMMAND_STANDARD_OUTPUT: Regex = """^\[[^]]+] \[[^]]+]: (.*)$""".toRegex(RegexOption.DOT_MATCHES_ALL)

        @JvmField
        public val RCON_STARTED_STANDARD_OUTPUT: Regex = """^\[[^]]+] \[[^]]+]: Thread RCON Listener started$""".toRegex(RegexOption.DOT_MATCHES_ALL)

        @JvmField
        public val PLAYER_MESSAGE_STANDARD_OUTPUT: Regex =
            """^\[.*?] \[.*?]:\s+(?:\[Not Secure]\s+)?<([^>]+)>(.*)$""".toRegex(RegexOption.DOT_MATCHES_ALL)

        @JvmField
        public val PLAYER_SAY_STANDARD_OUTPUT: Regex =
            """^\[.*?] \[.*?]:\s+(?:\[Not Secure]\s+)?\[([^>]+)](.*)$""".toRegex(RegexOption.DOT_MATCHES_ALL)

        @JvmField
        public val FETCH_ENTITY_DATA_OUTPUT: Regex = """(.*) has the following entity data$""".toRegex(RegexOption.DOT_MATCHES_ALL)
    }

    public open fun parseCommandStandardOutput(line: String): String {
        val result = COMMAND_STANDARD_OUTPUT.matchEntire(line) ?: return ""
        return result.groupValues[1]
    }

    public open fun isRconStartedStandardOutput(line: String): Boolean {
        return RCON_STARTED_STANDARD_OUTPUT.matches(line)
    }

    public open fun toCommandStandardInput(command: String): String { return command }

    public open fun matchPlayerMessageStandardOutput(line: String): Pair<String, String>? {
        val result = PLAYER_MESSAGE_STANDARD_OUTPUT.matchEntire(line) ?: PLAYER_SAY_STANDARD_OUTPUT.matchEntire(line) ?: return null
        val caller = result.groupValues[1]
        val message = result.groupValues[2].trimStart()
        return caller to message
    }

    public open fun buildFetchEntityDataCommand(selector: String): String {
        return "data get entity $selector"
    }

    public open fun parseFetchEntityDataOutput(line: String): String? {
        val result = FETCH_ENTITY_DATA_OUTPUT.matchEntire(line) ?: return null
        return result.groupValues[1]
    }

}