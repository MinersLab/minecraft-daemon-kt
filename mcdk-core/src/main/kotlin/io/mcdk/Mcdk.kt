package io.mcdk

import io.mcdk.api.command.CommandManager
import io.mcdk.api.permission.PermissionManager
import io.mcdk.plugin.PluginManager
import io.mcdk.plugin.SystemClassLoader
import io.mcdk.api.platform.Platform
import io.mcdk.util.configuration.ConfigManager
import io.mcdk.util.getValue
import io.mcdk.util.lateRef
import io.mcdk.util.logging.ILogger
import io.mcdk.util.logging.LoggerFactory
import io.mcdk.util.logging.getLogger
import io.mcdk.util.platform.findImplementation
import io.mcdk.util.ref
import java.io.File
import kotlin.reflect.full.isSubclassOf

public val mcdk: Mcdk by lateRef { IllegalStateException("Mcdk is not initialized yet") }

public class Mcdk(
    public val runtimeDirectory: File
) {
    public val logger: ILogger = LoggerFactory.getLogger<Mcdk>()

    public val configManager: ConfigManager = ConfigManager(this)
    public val pluginManager: PluginManager = PluginManager(this)
    public val commandManager: CommandManager = CommandManager(this)
    public val permissionManager: PermissionManager = PermissionManager(this)

    public val platform: Platform<*> by ref {
        val platformClass = Thread.currentThread().contextClassLoader.loadClass(McdkConfig.current.platform).kotlin
        if (!platformClass.isSubclassOf(Platform::class)) {
            throw IllegalStateException("Platform class ${platformClass.qualifiedName} does not implement Platform interface")
        }
        val implementation = if (platformClass.isSealed) findImplementation(platformClass) else platformClass
        implementation.constructors.first().call(this) as? Platform<*> ?: throw IllegalStateException("Platform class ${platformClass.qualifiedName} does not have a constructor that takes Mcdk as a parameter")
    }

    public fun start() {
        logger.info("Starting")
        val previousClassLoader = Thread.currentThread().contextClassLoader
        Thread.currentThread().contextClassLoader = SystemClassLoader(this)
        pluginManager.scan().forEach { pluginManager.add(it.first, it.second) }
        pluginManager.construct()
        pluginManager.load()
        commandManager.reload()
        platform.start()
        Thread.currentThread().contextClassLoader = previousClassLoader
    }

    public fun stop() {
        logger.info("Stopping")
        platform.dispose()
        pluginManager.dispose()
    }

}