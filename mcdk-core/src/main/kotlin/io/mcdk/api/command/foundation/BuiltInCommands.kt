package io.mcdk.api.command.foundation

import io.mcdk.api.command.ICommandRegistration

public object BuiltInCommands {

    public fun register(registration: ICommandRegistration) {
        HelpCommand.register(registration)
        McdkCommand.register(registration)
    }

}