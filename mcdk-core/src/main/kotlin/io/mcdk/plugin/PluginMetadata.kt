package io.mcdk.plugin

import io.mcdk.core.Identifier
import io.mcdk.util.platform.Version
import io.mcdk.util.platform.VersionRange
import kotlinx.serialization.Serializable

@Serializable
public data class PluginMetadata(
    val name: Identifier.Namespace,
    val version: Version,
    val entrypoint: String,
    val dependencies: List<Dependency> = emptyList(),
    val description: String? = null
) {

    @Serializable
    public data class Dependency(
        val name: Identifier.Namespace,
        val versionRange: VersionRange? = null
    )

}