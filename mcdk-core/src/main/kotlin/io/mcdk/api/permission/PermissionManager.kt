package io.mcdk.api.permission

import io.mcdk.Mcdk
import io.mcdk.core.Identifier
import io.mcdk.util.getValue
import io.mcdk.util.setValue
import kotlinx.serialization.json.JsonElement

@Suppress("DuplicatedCode")
public class PermissionManager(mcdk: Mcdk) {

    public var config: PermissionConfig by mcdk.configManager.getConfig(Identifier("permission"))


    public fun addGroupExtends(group: String, vararg extends: String) {
        config = config.apply { groups[group]?.extends += extends }
    }

    public fun removeGroupExtends(group: String, vararg extends: String) {
        config = config.apply { groups[group]?.extends -= extends }
    }

    public fun addGroupPermissions(group: String, vararg permissions: Pair<String, JsonElement>) {
        config = config.apply { groups[group]?.permissions += permissions }
    }

    public fun removeGroupPermissions(group: String, vararg permissions: String) {
        config = config.apply { groups[group]?.permissions -= permissions }
    }

    public fun createGroup(group: String) {
        config = config.apply { groups.putIfAbsent(group, PermissionConfig.Group()) }
    }

    public fun deleteGroup(group: String) {
        config = config.apply { groups -= group }
    }

    public fun createUser(user: String) {
        config = config.apply { users.putIfAbsent(user, PermissionConfig.User()) }
    }

    public fun deleteUser(user: String) {
        config = config.apply { users -= user }
    }

    public fun addUserGroups(user: String, vararg groups: String) {
        config = config.apply { users.putIfAbsent(user, PermissionConfig.User())?.groups += groups }
    }

    public fun removeUserGroups(user: String, vararg groups: String) {
        config = config.apply { users.putIfAbsent(user, PermissionConfig.User())?.groups -= groups }
    }

    public fun addUserPermissions(user: String, vararg permissions: Pair<String, JsonElement>) {
        config = config.apply { users.putIfAbsent(user, PermissionConfig.User())?.permissions += permissions }
    }

    public fun removeUserPermissions(user: String, vararg permissions: String) {
        config = config.apply { users.putIfAbsent(user, PermissionConfig.User())?.permissions -= permissions }
    }

    public fun getUserPermission(user: String, permission: String): JsonElement? = getUserPermissions(user)?.get(permission)
    public fun getGroupPermission(group: String, permission: String): JsonElement? = getGroupPermissions(group)?.get(permission)

    public fun getUserPermissions(user: String): Map<String, JsonElement>? {
        val user = config.users[user] ?: return null
        val userPerms = mutableMapOf<String, JsonElement>()
        for (group in user.groups) userPerms.putAll(getGroupPermissions(group) ?: continue)
        userPerms.putAll(user.permissions)
        return userPerms
    }

    public fun getGroupPermissions(group: String): Map<String, JsonElement>? {
        val group = config.groups[group] ?: return null
        val groupPerms = mutableMapOf<String, JsonElement>()
        for (parent in group.extends) groupPerms.putAll(getGroupPermissions(parent) ?: continue)
        groupPerms.putAll(group.permissions)
        return groupPerms
    }


}