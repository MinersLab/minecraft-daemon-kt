package io.mcdk.api.command.foundation

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.arguments.StringArgumentType.word
import io.mcdk.McdkConfig
import io.mcdk.api.command.ICommandRegistration
import io.mcdk.api.command.argument
import io.mcdk.api.command.node.IsServer
import io.mcdk.api.command.node.or
import io.mcdk.api.command.node.requires
import io.mcdk.api.command.register
import io.mcdk.api.permission.permission
import io.mcdk.api.text.AnnotatedText
import io.mcdk.api.text.ClickEvent
import io.mcdk.api.text.ListText
import io.mcdk.api.text.NamedTextColor
import io.mcdk.api.text.PlainText
import io.mcdk.api.text.RainbowText
import io.mcdk.core.Identifier
import io.mcdk.mcdk
import io.mcdk.plugin.PluginClassLoader
import io.mcdk.plugin.PluginStatus
import io.mcdk.util.configuration.configured
import kotlinx.serialization.json.Json

public object McdkCommand {

    public fun register(registration: ICommandRegistration): Unit = registration.register {
        literal("mcdk") {
            run {
                source.sender.sendFeedback {
                    RainbowText("Minecraft Daemon Kotlin (MCDK)")
                }
            }
            literal("perms") {
                val perms by lazy { mcdk.permissionManager }
                requires(permission("command.${Identifier.Namespace.DEFAULT}.mcdk.perms") or IsServer)
                literal("reload") {
                    run {
                        configured(perms::config).reload()
                    }
                }
                literal("group") {
                    run {
                        for ((groupName, _) in perms.config.groups) {
                            source.sender.sendFeedback {
                                AnnotatedText(
                                    color = NamedTextColor.GRAY,
                                    text = PlainText(groupName),
                                    clickEvent = ClickEvent.SuggestCommand(
                                        "${McdkConfig.current.commandPrefix.command}mcdk perms group $groupName list"
                                    )
                                )
                            }
                        }
                    }
                    argument("name", word()) {
                        suggests {
                            perms.config.groups.keys.forEach(it::suggest)
                        }
                        literal("list") {
                            run {
                                val name: String by argument()
                                val permissions = perms.getGroupPermissions(name) ?: return@run
                                for (permission in permissions.keys) {
                                    source.sender.sendFeedback {
                                        AnnotatedText(
                                            color = NamedTextColor.GRAY,
                                            text = PlainText(permission),
                                            clickEvent = ClickEvent.SuggestCommand(
                                                "${McdkConfig.current.commandPrefix.command}mcdk perms group $name get $permission"
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        literal("create") {
                            run {
                                val name: String by argument()
                                perms.createGroup(name)
                            }
                        }
                        literal("add-group") {
                            argument("group", word()) {
                                run {
                                    val group: String by argument("name")
                                    val parent: String by argument("group")
                                    perms.addGroupExtends(group, parent)
                                }
                            }
                        }
                        literal("remove-group") {
                            argument("group", word()) {
                                run {
                                    val group: String by argument("name")
                                    val parent: String by argument("group")
                                    perms.removeGroupExtends(group, parent)
                                }
                            }
                        }
                        literal("add") {
                            argument("permission", string()) {
                                argument("value", StringArgumentType.greedyString()) {
                                    run {
                                        val name: String by argument()
                                        val permission: String by argument()
                                        val value: String by argument()
                                        perms.addGroupPermissions(name, permission to Json.parseToJsonElement(value))
                                    }
                                }
                            }
                        }
                        literal("get") {
                            argument("permission", string()) {
                                run {
                                    val name: String by argument()
                                    val permission: String by argument()
                                    source.sender.sendFeedback {
                                        ListText(
                                            AnnotatedText(
                                                color = NamedTextColor.GREEN,
                                                text = PlainText("[$name] $permission"),
                                            ),
                                            AnnotatedText(
                                                color = NamedTextColor.GRAY,
                                                text = PlainText(" = "),
                                            ),
                                            AnnotatedText(
                                                color = NamedTextColor.YELLOW,
                                                text = PlainText("${perms.getGroupPermission(name, permission)}"),
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        literal("remove") {
                            argument("permission", string()) {
                                run {
                                    val name: String by argument()
                                    val permission: String by argument()
                                    perms.removeGroupPermissions(name, permission)
                                }
                            }
                        }
                    }
                }
                literal("user") {
                    run {
                        for ((userName, _) in perms.config.users) {
                            source.sender.sendFeedback {
                                AnnotatedText(
                                    color = NamedTextColor.GRAY,
                                    text = PlainText(userName),
                                    clickEvent = ClickEvent.SuggestCommand(
                                        "${McdkConfig.current.commandPrefix.command}mcdk perms user $userName list"
                                    )
                                )
                            }
                        }
                    }
                    argument("name", word()) {
                        suggests {
                            perms.config.users.keys.forEach(it::suggest)
                        }
                        literal("list") {
                            run {
                                val name: String by argument()
                                val permissions = perms.getUserPermissions(name) ?: return@run
                                for (permission in permissions.keys) {
                                    source.sender.sendFeedback {
                                        AnnotatedText(
                                            color = NamedTextColor.GRAY,
                                            text = PlainText(permission),
                                            clickEvent = ClickEvent.SuggestCommand(
                                                "${McdkConfig.current.commandPrefix.command}mcdk perms user $name get $permission"
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        literal("create") {
                            run {
                                val name: String by argument()
                                perms.createUser(name)
                            }
                        }
                        literal("add-group") {
                            argument("group", word()) {
                                run {
                                    val group: String by argument("name")
                                    val parent: String by argument("group")
                                    perms.addUserGroups(group, parent)
                                }
                            }
                        }
                        literal("remove-group") {
                            argument("group", word()) {
                                run {
                                    val group: String by argument("name")
                                    val parent: String by argument("group")
                                    perms.removeUserGroups(group, parent)
                                }
                            }
                        }
                        literal("add") {
                            argument("permission", string()) {
                                argument("value", StringArgumentType.greedyString()) {
                                    run {
                                        val name: String by argument()
                                        val permission: String by argument()
                                        val value: String by argument()
                                        perms.addUserPermissions(name, permission to Json.parseToJsonElement(value))
                                    }
                                }
                            }
                        }
                        literal("get") {
                            argument("permission", string()) {
                                run {
                                    val name: String by argument()
                                    val permission: String by argument()
                                    source.sender.sendFeedback {
                                        ListText(
                                            AnnotatedText(
                                                color = NamedTextColor.GREEN,
                                                text = PlainText("[$name] $permission"),
                                            ),
                                            AnnotatedText(
                                                color = NamedTextColor.GRAY,
                                                text = PlainText(" = "),
                                            ),
                                            AnnotatedText(
                                                color = NamedTextColor.YELLOW,
                                                text = PlainText("${perms.getUserPermission(name, permission)}"),
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        literal("remove") {
                            argument("permission", string()) {
                                run {
                                    val name: String by argument()
                                    val permission: String by argument()
                                    perms.removeUserPermissions(name, permission)
                                }
                            }
                        }
                    }
                }
            }
            literal("plugin") {
                requires(permission("command.${Identifier.Namespace.DEFAULT}.mcdk.plugin") or IsServer)
                literal("disable") {
                    argument("id", StringArgumentType.greedyString()) {
                        suggests {
                            for ((pluginName, plugin) in mcdk.pluginManager.plugins) {
                                if (plugin.status == PluginStatus.LOADED) {
                                    it.suggest(pluginName.value)
                                }
                            }
                        }
                        run {
                            val id: String by argument()
                            mcdk.pluginManager.config = mcdk.pluginManager.config.copy(
                                disabledPlugins = mcdk.pluginManager.config.disabledPlugins + Identifier.Namespace(id)
                            )
                            mcdk.pluginManager.dispose(Identifier.Namespace(id))
                        }
                    }
                }
                literal("enable") {
                    argument("file", StringArgumentType.greedyString()) {
                        suggests {
                            for (file in mcdk.pluginManager.scanFiles()) {
                                if (file !in mcdk.pluginManager.plugins.values.map(PluginClassLoader::file)) {
                                    it.suggest(file.relativeTo(mcdk.pluginManager.pluginDirectory).toString())
                                }
                            }
                        }
                        run {
                            val file: String by argument()
                            val pluginFile = mcdk.pluginManager.pluginDirectory.resolve(file)
                            val meta = mcdk.pluginManager.readMetadata(pluginFile)
                            mcdk.pluginManager.config = mcdk.pluginManager.config.copy(
                                disabledPlugins = mcdk.pluginManager.config.disabledPlugins - meta.name
                            )
                            mcdk.pluginManager.add(pluginFile, meta)
                        }
                    }
                }
                literal("load") {
                    argument("file", StringArgumentType.greedyString()) {
                        suggests {
                            for (file in mcdk.pluginManager.scanFiles()) {
                                if (file !in mcdk.pluginManager.plugins.values.map(PluginClassLoader::file)) {
                                    it.suggest(file.relativeTo(mcdk.pluginManager.pluginDirectory).toString())
                                }
                            }
                        }
                        run {
                            val fileName: String by argument("file")
                            val file = (mcdk.pluginManager.pluginDirectory.resolve(fileName))
                            mcdk.pluginManager.add(file)
                            mcdk.pluginManager.reload(file)
                        }
                    }
                }
                literal("unload") {
                    argument("id", StringArgumentType.greedyString()) {
                        suggests {
                            for ((pluginName, plugin) in mcdk.pluginManager.plugins) {
                                if (plugin.status == PluginStatus.LOADED) {
                                    it.suggest(pluginName.value)
                                }
                            }
                        }
                        run {
                            val id: String by argument()
                            mcdk.pluginManager.dispose(Identifier.Namespace(id))
                        }
                    }
                }

                literal("reload") {
                    argument("id", StringArgumentType.greedyString()) {
                        suggests {
                            for ((pluginName, _) in mcdk.pluginManager.plugins) {
                                it.suggest(pluginName.value)
                            }
                        }
                        run {
                            val id: String by argument()
                            mcdk.pluginManager.reload(Identifier.Namespace(id))
                        }
                    }
                }
                literal("list") {
                    literal("unloaded") {
                        run {
                            for ((file, meta) in mcdk.pluginManager.scan()) {
                                if (meta.name in mcdk.pluginManager.plugins) continue
                                val name = file.relativeTo(mcdk.pluginManager.pluginDirectory)
                                source.sender.sendFeedback {
                                    AnnotatedText(
                                        color = NamedTextColor.GRAY,
                                        text = PlainText("${meta.name.value} ($name)"),
                                        clickEvent = ClickEvent.SuggestCommand(
                                            "${McdkConfig.current.commandPrefix.command}mcdk plugin load $name"
                                        )
                                    )
                                }
                            }
                        }
                    }
                    run {
                        for ((pluginName, plugin) in mcdk.pluginManager.plugins) {
                            val textColor = when (plugin.status) {
                                PluginStatus.IDLE -> NamedTextColor.GRAY
                                PluginStatus.ERROR -> NamedTextColor.RED
                                PluginStatus.LOADING -> NamedTextColor.YELLOW
                                PluginStatus.LOADED -> NamedTextColor.GREEN
                                PluginStatus.CLOSED -> NamedTextColor.DARK_GRAY
                            }
                            val clickEvent = when (plugin.status) {
                                PluginStatus.IDLE -> ClickEvent.SuggestCommand(
                                    "${McdkConfig.current.commandPrefix.command}mcdk plugin load ${
                                        plugin.file.relativeTo(
                                            mcdk.pluginManager.pluginDirectory
                                        )
                                    }"
                                )

                                PluginStatus.LOADED -> ClickEvent.SuggestCommand(
                                    "${McdkConfig.current.commandPrefix.command}mcdk plugin disable ${plugin.metadata.name.value}"
                                )

                                else -> null
                            }
                            source.sender.sendFeedback {
                                AnnotatedText(
                                    color = textColor,
                                    text = PlainText(pluginName.value),
                                    clickEvent = clickEvent
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}