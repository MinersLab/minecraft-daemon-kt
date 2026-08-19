package io.mcdk.plugin.platform.vanilla.foundation

import io.mcdk.Mcdk
import io.mcdk.core.Identifier

public sealed class VanillaPlatform<T : VanillaConfig>(mcdk: Mcdk) : AbstractVanillaPlatform<T>(mcdk) {

    override val name: Identifier.Namespace = Identifier.Namespace("vanilla")

}