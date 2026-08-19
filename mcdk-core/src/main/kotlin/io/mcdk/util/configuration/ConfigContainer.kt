package io.mcdk.util.configuration

import io.mcdk.core.Identifier
import java.io.File
import java.io.InputStream
import java.io.OutputStream

public interface ConfigContainer {
    public fun <T> write(block: OutputStream.() -> T): T
    public fun <T> read(block: InputStream.() -> T): T

    public val isInitialized: Boolean
    public fun createConfig()
    public fun deleteConfig()
}

public class FileSystemConfigContainer(
    configManager: ConfigManager,
    public val name: Identifier,
    configType: ConfigType
) : ConfigContainer {

    public val file: File = configManager.configDirectory.resolve(name.namespace)
        .resolve(if (configType.extensionName != null) "${name.path}.${configType.extensionName}" else name.path)
        .also { it.parentFile.mkdirs() }

    override val isInitialized: Boolean = file.isFile

    override fun createConfig() {
        if (!file.isFile) {
            file.createNewFile()
        }
    }

    override fun deleteConfig() {
        if (file.isFile) {
            file.delete()
        }
    }

    override fun <T> read(block: InputStream.() -> T): T {
        return file.inputStream().use(block)
    }

    override fun <T> write(block: OutputStream.() -> T): T {
        return file.outputStream().use(block)
    }

}
