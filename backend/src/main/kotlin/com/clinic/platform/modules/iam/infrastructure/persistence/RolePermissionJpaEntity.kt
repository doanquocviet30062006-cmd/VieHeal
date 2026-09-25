package com.clinic.platform.modules.iam.infrastructure.persistence

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(
    name = "role_permissions",
    schema = "iam"
)
class RolePermissionJpaEntity(

    @EmbeddedId
    var id: RolePermissionId

) {

    protected constructor() : this(
        RolePermissionId()
    )
}