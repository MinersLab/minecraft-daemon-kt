package io.mcdk.api.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.tree.CommandNode
import io.mcdk.Mcdk
import io.mcdk.api.command.foundation.BuiltInCommands
import io.mcdk.api.command.session.CommandSource
import io.mcdk.api.platform.ServerEvents
import io.mcdk.plugin.PluginLifecycleListener
import io.mcdk.plugin.PluginLoadingContext
import java.util.concurrent.CompletableFuture

public interface ICommandRegistration {
    public fun register(command: LiteralArgumentBuilder<CommandSource>)
    public fun unregister(command: LiteralArgumentBuilder<CommandSource>)
    public fun reload()
    public val commands: Set<LiteralArgumentBuilder<CommandSource>>

    public fun parse(command: String, source: CommandSource?): ParseResults<CommandSource>?
    public fun getCompletions(parsed: ParseResults<CommandSource>, cursor: Int): CompletableFuture<Suggestions>
    public fun getSmartUsage(node: CommandNode<CommandSource>, source: CommandSource): Map<CommandNode<CommandSource>, String>?
    public fun getAllUsage(node: CommandNode<CommandSource>, source: CommandSource, restricted: Boolean): List<String>?
}

public class CommandRegistrationBlock(private val registration: ICommandRegistration) {

    public fun literal(name: String, block: CommandArgumentBuilder<CommandSource, LiteralArgumentBuilder<CommandSource>>.() -> Unit) {
        val context = CommandArgumentBuilder<CommandSource, LiteralArgumentBuilder<CommandSource>>(LiteralArgumentBuilder.literal(name))
        block(context)
        registration.register(context.builder() as LiteralArgumentBuilder<CommandSource>)
    }

}

public fun ICommandRegistration.register(block: CommandRegistrationBlock.() -> Unit) {
    val context = CommandRegistrationBlock(this)
    block(context)
}

public class CommandManager(private val mcdk: Mcdk) : ICommandRegistration {

    private val registered: MutableSet<LiteralArgumentBuilder<CommandSource>> = mutableSetOf()
    private var dispatcher: CommandDispatcher<CommandSource>? = null

    override val commands: Set<LiteralArgumentBuilder<CommandSource>>
        get() = registered.toSet()

    override fun parse(command: String, source: CommandSource?): ParseResults<CommandSource>? {
        return dispatcher?.parse(command, source)
    }

    override fun getCompletions(parsed: ParseResults<CommandSource>, cursor: Int): CompletableFuture<Suggestions> {
        return dispatcher?.getCompletionSuggestions(parsed, cursor) ?: Suggestions.empty()
    }

    override fun getSmartUsage(
        node: CommandNode<CommandSource>,
        source: CommandSource
    ): Map<CommandNode<CommandSource>, String>? {
        return dispatcher?.getSmartUsage(node, source)
    }

    override fun getAllUsage(
        node: CommandNode<CommandSource>,
        source: CommandSource,
        restricted: Boolean
    ): List<String>? {
        return dispatcher?.getAllUsage(node, source, restricted)?.toList()
    }

    public override fun reload() {
        val newDispatcher = CommandDispatcher<CommandSource>()
        registered.forEach { newDispatcher.register(it) }
        dispatcher = newDispatcher
    }

    public override fun register(command: LiteralArgumentBuilder<CommandSource>) {
        registered.add(command)
    }

    public override fun unregister(command: LiteralArgumentBuilder<CommandSource>) {
        registered.remove(command)
    }

    init {
        ServerEvents.ReceiveCommandEvent.EVENT.register { (command, sender, platform) ->
            if (dispatcher == null) return@register
            if (platform.mcdk != mcdk) return@register
            try {
                dispatcher!!.execute(
                    command,
                    CommandSource(platform, sender)
                )
            } catch (throwable: Throwable) {
                mcdk.logger.info("Failed to execute command: $command", throwable)
            }
        }
        BuiltInCommands.register(this)
    }

}

private class DelegatedCommandRegistration(
    pluginLoadingContext: PluginLoadingContext,
    private val manager: ICommandRegistration
) : ICommandRegistration by manager, PluginLifecycleListener {

    override val commands: Set<LiteralArgumentBuilder<CommandSource>>
        get() = manager.commands

    init {
        pluginLoadingContext.pluginLifecycleListeners.add(this)
    }

    private val registered: MutableSet<LiteralArgumentBuilder<CommandSource>> = mutableSetOf()

    override fun register(command: LiteralArgumentBuilder<CommandSource>) {
        manager.register(command)
        registered.add(command)
    }

    override fun unregister(command: LiteralArgumentBuilder<CommandSource>) {
        manager.unregister(command)
        registered.remove(command)
    }

    override fun onDispose() {
        registered.forEach { manager.unregister(it) }
        registered.clear()
        reload()
    }

}

public val ICommandRegistration.delegated: ICommandRegistration
    get() = DelegatedCommandRegistration(PluginLoadingContext.current, this)
