package io.mcdk.api.platform

import io.mcdk.Mcdk
import io.mcdk.api.text.Text
import io.mcdk.core.Identifier
import io.mcdk.util.logging.ILogger
import io.mcdk.util.logging.LoggerFactory

public abstract class Platform<C>(public val mcdk: Mcdk) {
    public val logger: ILogger = LoggerFactory.getLogger(this::class)

    public abstract val config: C
    public abstract val name: Identifier.Namespace

    protected var inputThread: Thread? = null
    protected var outputThread: Thread? = null

    protected abstract fun createInputThread()
    protected abstract fun createOutputThread()

    protected fun createThread() {
        createInputThread()
        createOutputThread()
    }

    public open fun start() {
        createThread()
    }

    public open fun dispose() {
        inputThread?.interrupt()
        outputThread?.interrupt()
        inputThread = null
        outputThread = null
    }

    public open suspend fun send(command: String, commandExecutionType: CommandExecutionType = CommandExecutionType.RCON) {
        retrieve(command, commandExecutionType)
    }

    public abstract suspend fun retrieve(command: String, commandExecutionType: CommandExecutionType = CommandExecutionType.RCON): String


    public abstract suspend fun sendMessage(playerName: String, message: Text)

}