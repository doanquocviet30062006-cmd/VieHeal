package com.clinic.platform.modules.iam.domain

import java.time.Instant
import java.util.UUID

data class IamRole(
    val id: UUID,
    val code: String,
    val name: String,
    val description: String?,
    val scope: RoleScope,
    val createdAt: Instant
)