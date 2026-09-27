package com.clinic.platform.modules.iam.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.UUID

@Embeddable
data class UserSystemRoleId(

    @Column(name = "user_id")
    var userId: UUID = UUID(0L, 0L),

    @Column(name = "role_id")
    var roleId: UUID = UUID(0L, 0L)

) : Serializable