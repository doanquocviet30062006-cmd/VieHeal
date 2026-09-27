package com.clinic.platform.modules.iam.domain

import java.util.UUID

interface UserSystemRoleRepository {

    fun findRoleIdsByUserId(
        userId: UUID
    ): List<UUID>
}