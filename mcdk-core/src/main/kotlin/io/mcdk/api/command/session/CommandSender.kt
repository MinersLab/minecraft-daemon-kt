package io.mcdk.api.command.session

import io.mcdk.api.platform.Platform
import io.mcdk.api.text.Text

public abstract class CommandSender {
    public abstract val name: String

    public abstract suspend fun sendFeedback(message: () -> Text)
}

public class ServerCommandSender : CommandSender() {
    override val name: String get() = "[Server]"

    override suspend fun sendFeedback(message: () -> Text) {
        println(message().toAnsi())
    }
}

public class PlayerCommandSender(
    playerName: String,
    private val platform: Platform<*>
) : CommandSender() {
    override val name: String = playerName

    override suspend fun sendFeedback(message: () -> Text) {
        platform.sendMessage(name, message())
    }
}
