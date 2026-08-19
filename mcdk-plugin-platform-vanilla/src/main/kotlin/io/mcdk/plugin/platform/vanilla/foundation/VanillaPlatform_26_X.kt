
package io.mcdk.plugin.platform.vanilla.foundation

import io.mcdk.Mcdk
import io.mcdk.core.Identifier
import io.mcdk.util.getValue
import io.mcdk.util.platform.OnlyIn

@OnlyIn("[26,)", "vanilla")
@Suppress("ClassName")
internal class VanillaPlatform_26_X(mcdk: Mcdk) : VanillaPlatform<VanillaConfig>(mcdk) {

    override val config: VanillaConfig by mcdk.configManager.getConfig(
        Identifier(name.value, "26.x")
    )

}