package io.mcdk.util.platform

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.jvm.jvmName

/**
 * 版本号类，用于表示和比较版本号。只允许包含数字、字母、点和连字符的版本号格式。
 */
@Serializable(Version.Serializer::class)
public data class Version(public val version: String) : Comparable<Version> {
    public object Serializer : KSerializer<Version> {

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(Version::class.jvmName, PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): Version {
            val versionString = decoder.decodeString()
            return Version(versionString)
        }

        override fun serialize(encoder: Encoder, value: Version) {
            encoder.encodeString(value.version)
        }
    }

    init {
        require(version.matches(Regex("^[0-9A-Za-z.-]+$"))) {
            "Invalid version format: $version. Only digits, letters, dots, and hyphens are allowed."
        }
    }

    public override operator fun compareTo(other: Version): Int {
        val thisParts = this.version.split(".")
        val otherParts = other.version.split(".")

        val maxLength = maxOf(thisParts.size, otherParts.size)
        for (i in 0 until maxLength) {
            val thisPart = thisParts.getOrNull(i)?.toIntOrNull() ?: 0
            val otherPart = otherParts.getOrNull(i)?.toIntOrNull() ?: 0

            if (thisPart != otherPart) {
                return thisPart - otherPart
            }
        }
        return 0
    }

}

public operator fun Version?.rangeTo(other: Version?): VersionRange {
    return VersionRange(this, other)
}

public operator fun Version?.rangeUntil(other: Version?): VersionRange {
    return VersionRange(this, other, includeMax = false)
}
