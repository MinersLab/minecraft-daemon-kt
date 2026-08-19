package io.mcdk.api.command

import com.mojang.brigadier.RedirectModifier
import com.mojang.brigadier.SingleRedirectModifier
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.CommandNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KProperty

public open class CommandArgumentBuilder<S, T : ArgumentBuilder<S, T>>(protected val builder: ArgumentBuilder<S, T>) {

    public open fun literal(
        name: String,
        block: CommandArgumentBuilder<S, LiteralArgumentBuilder<S>>.() -> Unit
    ): CommandNode<S> {
        val context = CommandArgumentBuilder<S, LiteralArgumentBuilder<S>>(literal(name))
        block(context)
        val built = context.build()
        builder.then(built)
        return built
    }

    public open fun requires(block: S.() -> Boolean): CommandNode<S> = builder.requires(block).build()

    public open fun fork(target: CommandNode<S>, modifier: RedirectModifier<S>): CommandNode<S> =
        builder.fork(target, modifier).build()

    public open fun redirect(target: CommandNode<S>, modifier: SingleRedirectModifier<S>? = null): CommandNode<S> =
        if (modifier != null) builder.redirect(target, modifier).build()
        else builder.redirect(target).build()

    public open fun <T> argument(
        name: String,
        type: ArgumentType<T>,
        block: CommandRequiredArgumentBuilder<S, T>.() -> Unit
    ): CommandNode<S?> {
        val context = CommandRequiredArgumentBuilder(argument<S, T>(name, type))
        block(context)
        val built = context.build()
        builder.then(built)
        return built
    }

    public open fun execute(block: suspend CommandContext<S>.() -> Int) {
        builder.executes {
            runBlocking { it.block() }
        }
    }

    public open fun run(block: suspend CommandContext<S>.() -> Unit) {
        builder.executes {
            try {
                runBlocking {
                    it.block()
                }
                0
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                1
            }
        }
    }

    public fun builder(): ArgumentBuilder<S, T> = builder
    public fun build(): CommandNode<S> = builder.build()
}

public open class CommandRequiredArgumentBuilder<S, T>(protected val requiredBuilder: RequiredArgumentBuilder<S, T>) :
    CommandArgumentBuilder<S, RequiredArgumentBuilder<S, T>>(requiredBuilder) {

    public open fun suggests(block: suspend CommandContext<S>.(builder: SuggestionsBuilder) -> Unit) {
        requiredBuilder.suggests { ctx, builder ->
            CoroutineScope(Dispatchers.IO).future {
                block(ctx, builder)
                builder.build()
            }
        }
    }

}


public class ArgumentDelegate<S, T>(public val context: CommandContext<S>, public val name: String? = null, public val type: Class<T>) {

    public operator fun getValue(thisRef: Any?, property: KProperty<*>): T = context.getArgument(name ?: property.name, type)

}

public inline fun <S, reified T> CommandContext<S>.argument(name: String? = null): ArgumentDelegate<S, T> = ArgumentDelegate(this, name, T::class.java)
public inline fun <S, reified T> CommandContext<S>.getArgument(name: String): T = getArgument(name, T::class.java)
