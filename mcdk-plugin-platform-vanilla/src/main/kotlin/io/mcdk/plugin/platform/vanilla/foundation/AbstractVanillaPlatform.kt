package io.mcdk.plugin.platform.vanilla.foundation

import com.mojang.brigadier.suggestion.Suggestion
import io.mcdk.Mcdk
import io.mcdk.McdkConfig
import io.mcdk.api.command.session.CommandSource
import io.mcdk.api.command.session.PlayerCommandSender
import io.mcdk.api.command.session.ServerCommandSender
import io.mcdk.api.platform.CommandExecutionType
import io.mcdk.api.platform.Platform
import io.mcdk.api.platform.ServerEvents
import io.mcdk.api.platform.rcon.RconClient
import io.mcdk.api.text.Text
import io.mcdk.plugin.platform.vanilla.helper.VanillaHelper
import io.mcdk.plugin.platform.vanilla.helper.text.TextSerializer
import io.mcdk.util.platform.findImplementation
import kotlinx.io.IOException
import org.jline.reader.Candidate
import org.jline.reader.Completer
import org.jline.reader.LineReaderBuilder
import org.jline.reader.impl.DefaultParser
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import java.io.File
import java.nio.charset.Charset
import java.util.StringTokenizer
import kotlin.concurrent.thread
import kotlin.reflect.full.createInstance

public abstract class AbstractVanillaPlatform<T : VanillaConfig>(mcdk: Mcdk) : Platform<T>(mcdk) {

    protected var process: Process? = null
    protected var rconClient: RconClient? = null
    public open val helper: VanillaHelper = VanillaHelper()

    override fun start() {
        val tokenizer = StringTokenizer(McdkConfig.current.commandLine)
        val command = Array(tokenizer.countTokens()) { tokenizer.nextToken() }
        process = ProcessBuilder(*command)
            .directory(mcdk.runtimeDirectory.resolve("server").also(File::mkdirs))
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectInput(ProcessBuilder.Redirect.PIPE)
            .redirectErrorStream(true)
            .start()
        process?.onExit()
            ?.thenRun(mcdk::stop)
        super.start()
    }

    protected fun createConsoleReader(terminalConfiguration: (TerminalBuilder) -> Unit = {}): LineReaderBuilder {
        val terminal: Terminal = TerminalBuilder.builder()
            .system(true)
            .also(terminalConfiguration)
            .build()

        val completer = Completer { _, line, candidates ->
            val text = line.line()
            if (text.isBlank()) {
                candidates.add(Candidate("/"))
            }
            if (text.startsWith(McdkConfig.current.commandPrefix.command)) return@Completer
            val cursor = line.cursor()
            val source = CommandSource(this, ServerCommandSender())
            val parsed = mcdk.commandManager.parse(text, source)
                ?: return@Completer
            val suggestions = mcdk.commandManager.getCompletions(parsed, cursor)
                .get()
            suggestions
                .list
                .map(Suggestion::getText)
                .map(::Candidate)
                .forEach(candidates::add)
        }

        val reader = LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(completer)
            .parser(
                object : DefaultParser() { override fun isEscapeChar(ch: Char) = false }
            )
        //.option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
        return reader
    }

    override fun createInputThread() {
        inputThread = thread(name = "input-handler", isDaemon = true) {
            val lineReader = createConsoleReader().build()
            while (inputThread?.isAlive != false) {
                val line = try {
                    lineReader.readLine() ?: break
                } catch (_: Throwable) {
                    break
                }
                try {
                    onInput(line)
                } catch (e: IOException) {
                    logger.error("Failed to write to server process", e)
                    break
                }
            }
        }
        if (config.rcon.enabled) {
            rconClient = RconClient(
                host = config.rcon.host,
                port = config.rcon.port,
                password = config.rcon.password
            )
        }
    }

    override fun createOutputThread() {
        outputThread = thread(name = "output-handler") {
            if (process == null) {
                return@thread
            }
            val reader = process!!.inputStream.bufferedReader(Charset.forName(config.outputCharset))
            while (process!!.isAlive) {
                val line = reader.readLine() ?: break
                onOutput(line)
            }
            reader.close()
        }
    }

    @Suppress("KotlinPrintToLogpoint")
    protected fun onOutput(line: String) {
        println(line)
        if (rconClient?.isAlive != true && helper.isRconStartedStandardOutput(line)) {
            rconClient?.connect()
            return
        }
        helper.matchPlayerMessageStandardOutput(line)?.let { (caller, message) ->
            if (message.startsWith(McdkConfig.current.commandPrefix.command)) {
                ServerEvents.ReceiveCommandEvent(
                    message.removePrefix(McdkConfig.current.commandPrefix.command).trim(),
                    PlayerCommandSender(caller, this),
                    this
                ).fire()
            }
        }
    }

    protected fun onInput(line: String) {
        if (line.startsWith(McdkConfig.current.commandPrefix.game)) {
            process?.outputStream?.write((line.removePrefix(McdkConfig.current.commandPrefix.game).trim() + "\n").toByteArray(Charset.forName(config.inputCharset)))
            process?.outputStream?.flush()
        } else {
            ServerEvents.ReceiveCommandEvent(
                line.trim(),
                ServerCommandSender(),
                this
            ).fire()
        }
    }

    protected open fun writeToProcess(command: String) {
        process?.outputStream?.write((helper.toCommandStandardInput(command) + "\n").toByteArray(Charset.forName(config.inputCharset)))
        process?.outputStream?.flush()
    }

    override fun dispose() {
        super.dispose()
        rconClient?.close()
        process?.destroy()
        rconClient = null
        process = null
    }

    protected open fun retrieveFromRcon(command: String): String =
        (rconClient ?: throw IllegalStateException("Rcon client is not initialized")).retrieve(command)

    protected open fun retrieveFromProcess(): String {
        val reader = process?.inputStream?.bufferedReader()
        val line = reader?.readLine()?.also(::onOutput) ?: throw IllegalStateException("Failed to read from process")
        return helper.parseCommandStandardOutput(line)
    }

    override suspend fun send(command: String, commandExecutionType: CommandExecutionType) {
        when (commandExecutionType) {
            CommandExecutionType.RCON if rconClient?.isAlive == true -> retrieveFromRcon(command)
            else -> writeToProcess(command)
        }
    }

    override suspend fun retrieve(command: String, commandExecutionType: CommandExecutionType): String {
        return when (commandExecutionType) {
            CommandExecutionType.RCON if rconClient?.isAlive == true -> retrieveFromRcon(command)
            else -> {
                writeToProcess(command)
                retrieveFromProcess()
            }
        }
    }

    override suspend fun sendMessage(playerName: String, message: Text) {
        val textSerializer: TextSerializer = findImplementation<TextSerializer>().createInstance()
        send(
            "tellraw $playerName ${textSerializer.toJson(message)}"
        )
    }

}
