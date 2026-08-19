package io.mcdk.core

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*
import kotlin.reflect.jvm.jvmName

public class Identifier(
    namespace: Namespace,
    path: Path
) {

    public constructor(path: Path) : this(Namespace.DEFAULT, path)
    public constructor(path: String) : this(Namespace.DEFAULT, Path(path))

    public val namespace: String = namespace.value
    public val path: String = path.value

    public fun splitPath(): List<String> = path.split('/').filter(String::isNotEmpty)

    @Serializable(Namespace.NamespaceSerializer::class)
    public data class Namespace(public val value: String) {
        public companion object {
            @JvmField
            public val DEFAULT: Namespace = Namespace("mcdk")
        }

        public object NamespaceSerializer : KSerializer<Namespace> {
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor(Namespace::class.jvmName, PrimitiveKind.STRING)

            override fun deserialize(decoder: Decoder): Namespace = Namespace(decoder.decodeString())
            override fun serialize(encoder: Encoder, value: Namespace) {
                encoder.encodeString(value.value)
            }
        }

        init {
            require(isValidNamespace(value)) { "Invalid namespace: '$value'. Namespace must be non-empty and can only contain lowercase letters, digits, underscores, and hyphens." }
        }

        override fun toString(): String = value
    }

    @Serializable(Path.PathSerializer::class)
    public data class Path(public val value: String) {
        public object PathSerializer : KSerializer<Path> {
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor(Path::class.jvmName, PrimitiveKind.STRING)

            override fun deserialize(decoder: Decoder): Path = Path(decoder.decodeString())
            override fun serialize(encoder: Encoder, value: Path) {
                encoder.encodeString(value.value)
            }
        }

        init {
            require(isValidPath(value)) { "Invalid path: '$value'. Path must be non-empty and can only contain lowercase letters, digits, underscores, hyphens, and slashes." }
        }

        override fun toString(): String = value
    }

    public companion object {
        public fun parse(input: String): Option<Identifier> =
            runCatching { parseOrThrow(input) }.getOrNull()?.let(::Some) ?: None

        public fun parseOrThrow(input: String): Identifier {
            val parts = input.split(':', limit = 2)
            require(parts.size == 1 || parts.size == 2) { "Invalid identifier format: '$input'. Expected format is 'namespace:path'." }
            if (parts.size == 2) {
                val (namespace, path) = parts
                return Identifier(namespace, path)
            } else {
                val path = parts[0]
                return Identifier(path)
            }
        }

        public fun isValidNamespace(namespace: String): Boolean =
            namespace.isNotEmpty() && namespace.all { it.isLowerCase() || it.isDigit() || it == '_' || it == '-' }

        public fun isValidPath(path: String): Boolean =
            path.isNotEmpty() && path.all { it.isLowerCase() || it.isDigit() || it == '_' || it == '-' || it == '/' || it == '.' }
    }

    public constructor(namespace: String, path: String) : this(Namespace(namespace), Path(path))

    override fun toString(): String = "$namespace:$path"
    override fun equals(other: Any?): Boolean =
        other is Identifier && this.namespace == other.namespace && this.path == other.path

    override fun hashCode(): Int = Objects.hash(namespace, path)

    public fun format(formatter: (namespace: String, path: String) -> String): String = formatter(namespace, path)

}
