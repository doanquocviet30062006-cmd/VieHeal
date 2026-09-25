package com.clinic.platform.modules.iam.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.UUID

@Embeddable
data class RolePermissionId(

    @Column(name = "role_id")
    var roleId: UUID = UUID(0L, 0L),

    @Column(name = "permission_id")
    var permissionId: UUID = UUID(0L, 0L)

) : Serializable