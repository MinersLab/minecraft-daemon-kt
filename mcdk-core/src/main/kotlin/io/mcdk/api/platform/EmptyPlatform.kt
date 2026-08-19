package io.mcdk.api.platform

import io.mcdk.Mcdk
import io.mcdk.api.text.Text
import io.mcdk.core.Identifier

public class EmptyPlatform(mcdk: Mcdk) : Platform<Nothing?>(mcdk) {
    public companion object {
        @JvmField
        public val NAME: Identifier.Namespace = Identifier.Namespace("empty")
    }

    override val name: Identifier.Namespace = NAME
    override val config: Nothing? = null

    override fun createInputThread() {}
    override fun createOutputThread() {}

    override suspend fun retrieve(command: String, commandExecutionType: CommandExecutionType): String = ""

    override suspend fun sendMessage(playerName: String, message: Text) {}

}