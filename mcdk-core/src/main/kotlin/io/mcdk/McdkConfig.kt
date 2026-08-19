package io.mcdk

import io.mcdk.core.Identifier
import io.mcdk.api.platform.EmptyPlatform
import io.mcdk.util.platform.Version
import kotlinx.serialization.Serializable
import kotlin.reflect.jvm.jvmName

@Serializable
public data class McdkConfig(
    public val mcVersion: Version = Version("26.2"),
    public val platform: String = EmptyPlatform::class.jvmName,
    public val commandLine: String = "",
    public val commandPrefix: CommandPrefixConfig = CommandPrefixConfig()
) {

    @Serializable
    public data class CommandPrefixConfig(
        public val game: String = "/",
        public val command: String = "!!"
    )

    public companion object {

        public val current: McdkConfig
            get() = mcdk.configManager.getConfig<McdkConfig>(Identifier("core")).get()
    }

}
