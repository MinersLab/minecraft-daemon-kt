package io.mcdk.api.command.node

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.tree.CommandNode
import io.mcdk.api.command.CommandArgumentBuilder
import io.mcdk.api.command.session.CommandSource
import io.mcdk.api.command.session.PlayerCommandSender
import io.mcdk.api.command.session.ServerCommandSender

public fun interface Requirement {
    public fun test(source: CommandSource): Boolean
}

public object IsPlayer : Requirement {
    override fun test(source: CommandSource): Boolean = source.sender is PlayerCommandSender
}

public object IsServer : Requirement {
    override fun test(source: CommandSource): Boolean = source.sender is ServerCommandSender
}

public operator fun Requirement.not(): Requirement {
    val requirement = this
    return Requirement { !requirement.test(it) }
}

public infix fun Requirement.and(other: Requirement): Requirement {
    val requirement = this
    return Requirement { requirement.test(it) && other.test(it) }
}

public infix fun Requirement.or(other: Requirement): Requirement {
    val requirement = this
    return Requirement { requirement.test(it) || other.test(it) }
}

public fun <T : ArgumentBuilder<CommandSource, T>> CommandArgumentBuilder<CommandSource, T>.requires(requirement: Requirement): CommandNode<CommandSource> =
    requires { requirement.test(this) }
