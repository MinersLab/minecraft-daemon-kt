package io.mcdk.api.command.foundation

import io.mcdk.api.command.ICommandRegistration
import io.mcdk.api.command.register
import io.mcdk.api.text.RainbowText

public object McdkCommand {

    public fun register(registration: ICommandRegistration): Unit = registration.register {
        literal("mcdk") {
            run {
                source.sender.sendFeedback {
                    RainbowText("Minecraft Daemon Kotlin (MCDK)")
                }
            }
        }
    }

}