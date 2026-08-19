package io.mcdk.plugin.platform.vanilla.foundation

import kotlinx.serialization.Serializable

@Serializable
public open class VanillaConfig {
    public var inputCharset: String = Charsets.UTF_8.name()
    public var outputCharset: String = Charsets.UTF_8.name()

    public var rcon: RconConfig = RconConfig()

    @Serializable
    public open class RconConfig {
        public var enabled: Boolean = false
        public var host: String = "localhost"
        public var port: Int = 25575
        public var password: String = ""
    }
}