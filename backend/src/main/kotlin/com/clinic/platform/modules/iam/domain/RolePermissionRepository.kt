package com.clinic.platform.modules.iam.domain

import java.util.UUID

interface RolePermissionRepository {

    fun findPermissionIdsByRoleIds(
        roleIds: Collection<UUID>
    ): Set<UUID>
}