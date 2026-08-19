package io.mcdk.util.platform

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
import java.nio.CharBuffer
import kotlin.reflect.jvm.jvmName

@Serializable(VersionRange.Serializer::class)
public data class VersionRange(
    public val min: Version? = null,
    public val max: Version? = null,
    public val includeMin: Boolean = true,
    public val includeMax: Boolean = true
) : Comparable<VersionRange> {

    public object Serializer : KSerializer<VersionRange> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(VersionRange::class.jvmName, PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: VersionRange) {
            encoder.encodeString(value.toString())
        }

        override fun deserialize(decoder: Decoder): VersionRange {
            val rangeStr = decoder.decodeString()
            return parseOrThrow(rangeStr)
        }
    }

    public companion object {
        @JvmField
        public val ALL: VersionRange = VersionRange()

        @JvmStatic
        public val NONE: VersionRange = VersionRange(min = null, max = null, includeMin = false, includeMax = false)


        public fun parse(range: String): Option<VersionRange> =
            try {
                Some(parseOrThrow(range))
            } catch (_: Throwable) {
                None
            }

        public fun parseOrThrow(range: String): VersionRange {
            val charBuffer = CharBuffer.wrap(range)
            val includeMin = when (charBuffer.get()) {
                '[' -> true
                '(' -> false
                else -> throw IllegalArgumentException("Invalid range format: $range")
            }
            val minVersion = buildString {
                while (charBuffer.hasRemaining()) {
                    val c = charBuffer.get()
                    if (c == ',') break
                    append(c)
                }
            }.takeUnless(String::isEmpty)?.let(::Version)
            val maxVersion = buildString {
                while (charBuffer.hasRemaining()) {
                    val c = charBuffer.get()
                    if (c == ']' || c == ')') {
                        charBuffer.position(charBuffer.position() - 1)
                        break
                    }
                    append(c)
                }
            }.takeUnless(String::isEmpty)?.let(::Version)
            val includeMax = when (charBuffer.get()) {
                ']' -> true
                ')' -> false
                else -> throw IllegalArgumentException("Invalid range format: $range")
            }
            return VersionRange(minVersion, maxVersion, includeMin, includeMax)
        }
    }

    public override fun toString(): String {
        val minStr = min?.toString() ?: ""
        val maxStr = max?.toString() ?: ""
        val minBracket = if (includeMin) "[" else "("
        val maxBracket = if (includeMax) "]" else ")"
        return "$minBracket$minStr,$maxStr$maxBracket"
    }

    public operator fun contains(version: Version): Boolean {
        val minCheck = min?.let { if (includeMin) version >= it else version > it } ?: true
        val maxCheck = max?.let { if (includeMax) version <= it else version < it } ?: true
        return minCheck && maxCheck
    }

    /**
     * 比较两个版本范围的大小。
     */
    public override operator fun compareTo(other: VersionRange): Int {
        val minComparison = when {
            this.min == null && other.min == null -> 0
            this.min == null -> -1
            other.min == null -> 1
            else -> this.min.compareTo(other.min)
        }
        if (minComparison != 0) return minComparison

        val maxComparison = when {
            this.max == null && other.max == null -> 0
            this.max == null -> 1
            other.max == null -> -1
            else -> this.max.compareTo(other.max)
        }
        return maxComparison
    }

}