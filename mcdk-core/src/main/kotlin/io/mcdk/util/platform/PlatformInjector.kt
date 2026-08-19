package io.mcdk.util.platform

import io.mcdk.McdkConfig
import io.mcdk.core.Identifier
import io.mcdk.mcdk
import io.mcdk.util.ref
import kotlin.reflect.KClass

public fun <T : Any> findImplementation(
    kClass: KClass<T>,
    mcVersion: Version = McdkConfig.current.mcVersion,
    platform: Identifier.Namespace? = if (ref(mcdk::platform).hasValue()) mcdk.platform.name else null
): KClass<out T> {
    val sealedSubclasses = kClass.sealedSubclasses
    val matchingSubclass = sealedSubclasses.mapNotNull { subclass ->
        val annotation = subclass.annotations.filterIsInstance<OnlyIn>().firstOrNull() ?: return@mapNotNull null
        Triple(subclass, annotation, VersionRange.parseOrThrow(annotation.mcVersionRange))
    }.filter { (_, annotation, versionRange) ->
        mcVersion in versionRange && (platform == null || platform.value in annotation.platforms)
    }.minWithOrNull { (_, _, range1), (_, _, range2) ->
        range1.compareTo(range2)
    } ?: throw IllegalStateException("No implementation found for ${kClass.simpleName} for Minecraft version $mcVersion and platform ${platform?.value}")
    return matchingSubclass.first
}

public inline fun <reified T : Any> findImplementation(
    mcVersion: Version = McdkConfig.current.mcVersion,
    platform: Identifier.Namespace? = if (ref(mcdk::platform).hasValue()) mcdk.platform.name else null
): KClass<out T> = findImplementation(T::class, mcVersion, platform)
