package com.clinic.platform.modules.iam.domain

import java.util.UUID

interface IamPermissionRepository {

    fun findById(
        id: UUID
    ): IamPermission?

    fun findAllByIds(
        ids: Collection<UUID>
    ): List<IamPermission>
}