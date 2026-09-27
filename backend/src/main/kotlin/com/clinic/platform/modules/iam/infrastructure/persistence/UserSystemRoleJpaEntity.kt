package com.clinic.platform.modules.iam.infrastructure.persistence

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(
    name = "user_system_roles",
    schema = "iam"
)
class UserSystemRoleJpaEntity(

    @EmbeddedId
    var id: UserSystemRoleId

) {

    protected constructor() : this(
        UserSystemRoleId()
    )
}