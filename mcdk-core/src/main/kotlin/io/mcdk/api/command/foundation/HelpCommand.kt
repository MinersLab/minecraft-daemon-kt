package io.mcdk.api.command.foundation

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.CommandNode
import io.mcdk.McdkConfig
import io.mcdk.api.command.ICommandRegistration
import io.mcdk.api.command.argument
import io.mcdk.api.command.register
import io.mcdk.api.text.AnnotatedText
import io.mcdk.api.text.ClickEvent
import io.mcdk.api.text.ListText
import io.mcdk.api.text.NamedTextColor
import io.mcdk.api.text.PlainText

public object HelpCommand {

    public fun register(registration: ICommandRegistration): Unit = registration.register {
        literal("help") {
            run {
                for (command in rootNode.children) {
                    if (!command.requirement.test(source)) continue
                    source.sender.sendFeedback {
                        AnnotatedText(
                            text = ListText(
                                AnnotatedText(
                                    color = NamedTextColor.GREEN,
                                    text = PlainText(command.name)
                                ),
                                PlainText(" "),
                                AnnotatedText(
                                    color = NamedTextColor.GRAY,
                                    text = PlainText(registration.getSmartUsage(command, source)!!.toList().joinToString(separator = " ") { it.second })
                                )
                            ),
                            clickEvent = ClickEvent.SuggestCommand(
                                command = "${McdkConfig.current.commandPrefix.command}help ${command.name}"
                            ),
                            insertion = "${McdkConfig.current.commandPrefix.command}help ${command.name}"
                        )
                    }
                }
            }
            argument("command", StringArgumentType.greedyString()) {
                suggests {
                    rootNode.children
                        .map(CommandNode<*>::getName)
                        .forEach(it::suggest)
                }
                run {
                    val command: String by argument()
                    val node = rootNode.getChild(command)
                    if (!node.requirement.test(source)) return@run
                    for (usage in registration.getAllUsage(node, source, true) ?: emptyList()) {
                        source.sender.sendFeedback {
                            AnnotatedText(
                                text = ListText(
                                    AnnotatedText(
                                        color = NamedTextColor.GREEN,
                                        text = PlainText(node.name)
                                    ),
                                    PlainText(" "),
                                    AnnotatedText(
                                        color = NamedTextColor.GRAY,
                                        text = PlainText(usage)
                                    )
                                ),
                                clickEvent = ClickEvent.SuggestCommand(
                                    command = "${McdkConfig.current.commandPrefix.command}${node.name} $usage"
                                ),
                                insertion = "${McdkConfig.current.commandPrefix.command}${node.name} $usage"
                            )
                        }
                    }
                }
            }
        }
    }

}