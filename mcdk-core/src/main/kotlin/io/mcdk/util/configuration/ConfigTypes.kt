package io.mcdk.util.configuration

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream

public object ConfigTypes {

    @OptIn(ExperimentalSerializationApi::class)
    @JvmField
    public val Json: ConfigType = object : ConfigType {
        val json = Json {
            prettyPrint = true
            prettyPrintIndent = " ".repeat(4)
            encodeDefaults = true
        }

        override fun <T> encodeToStream(value: T, stream: OutputStream, serializer: SerializationStrategy<T>) {
            json.encodeToStream(serializer, value, stream)
        }

        override fun <T> decodeFromStream(stream: InputStream, serializer: DeserializationStrategy<T>): T =
            json.decodeFromStream(serializer, stream)

        override val extensionName: String = "json"
    }

    @OptIn(ExperimentalSerializationApi::class)
    @JvmField
    public val Jsonc : ConfigType = object : ConfigType {
        val json = Json {
            prettyPrint = true
            prettyPrintIndent = " ".repeat(4)
            encodeDefaults = true
            allowComments = true
        }

        override fun <T> encodeToStream(value: T, stream: OutputStream, serializer: SerializationStrategy<T>) {
            json.encodeToStream(serializer, value, stream)
        }

        override fun <T> decodeFromStream(stream: InputStream, serializer: DeserializationStrategy<T>): T =
            json.decodeFromStream(serializer, stream)

        override val extensionName: String = "jsonc"
    }

}