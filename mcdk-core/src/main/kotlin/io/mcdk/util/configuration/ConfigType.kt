package io.mcdk.util.configuration

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import java.io.InputStream
import java.io.OutputStream

public interface ConfigType {
    public fun <T> encodeToStream(value: T, stream: OutputStream, serializer: SerializationStrategy<T>)
    public fun <T> decodeFromStream(stream: InputStream, serializer: DeserializationStrategy<T>): T

    public val extensionName: String?
}

private class ConfigTypeWithExtension(
    extension: String?,
    delegated: ConfigType,
) : ConfigType by delegated {

    override val extensionName: String? = extension

}

public fun ConfigType.withExtension(extension: String): ConfigType = ConfigTypeWithExtension(extension, this)
