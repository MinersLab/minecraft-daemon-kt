package io.mcdk.api.permission

import io.mcdk.api.command.node.Requirement
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull

/**
 * 要求命令调用者拥有某些权限
 */
public fun permission(permission: String): Requirement =
    permission(permission) { !(it == null || (it is JsonPrimitive && (it.doubleOrNull == 0.0 || it.booleanOrNull == false || it.content.isEmpty()))) }

/**
 * 对命令调用者拥有权限进行判断
 */
public fun permission(permission: String, predicate: (JsonElement?) -> Boolean): Requirement = {
    predicate(it.server.mcdk.permissionManager.getUserPermission(it.sender.name, permission))
}