package io.mcdk.plugin.platform.vanilla.helper.data

import io.mcdk.plugin.platform.vanilla.foundation.AbstractVanillaPlatform
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.nbt.TagStringIO

public suspend fun AbstractVanillaPlatform<*>.fetchEntityData(selector: String): CompoundBinaryTag {
    val rawResult = retrieve(helper.buildFetchEntityDataCommand(selector))
    val snbt = helper.parseFetchEntityDataOutput(rawResult) ?: throw IllegalStateException("Failed to parse entity data output: $rawResult")
    return TagStringIO.tagStringIO().asCompound(snbt)
}