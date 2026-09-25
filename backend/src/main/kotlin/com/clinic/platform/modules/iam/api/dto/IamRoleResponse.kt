package com.clinic.platform.modules.iam.api.dto

import com.clinic.platform.modules.iam.domain.IamRole
import com.clinic.platform.modules.iam.domain.RoleScope
import java.time.Instant
import java.util.UUID

data class IamRoleResponse(
    val id: UUID,
    val code: String,
    val name: String,
    val description: String?,
    val scope: RoleScope,
    val createdAt: Instant
) {

    companion object {

        fun fromDomain(
            role: IamRole
        ): IamRoleResponse {

            return IamRoleResponse(
                id = role.id,
                code = role.code,
                name = role.name,
                description = role.description,
                scope = role.scope,
                createdAt = role.createdAt
            )
        }
    }
}