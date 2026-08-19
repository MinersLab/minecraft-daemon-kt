package io.mcdk.plugin

import io.mcdk.core.Identifier
import kotlinx.serialization.Serializable

@Serializable
public data class PluginManagerConfig(
    public val disabledPlugins: Set<Identifier.Namespace> = emptySet()
)