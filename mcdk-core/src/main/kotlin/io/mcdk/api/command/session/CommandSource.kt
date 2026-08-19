package io.mcdk.api.command.session

import io.mcdk.api.platform.Platform

public open class CommandSource(
    public val server: Platform<*>,
    public val sender: CommandSender
)
