package com.clinic.platform.modules.iam.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.UUID

@Embeddable
data class MembershipRoleId(

    @Column(name = "membership_id")
    var membershipId: UUID = UUID(0L, 0L),

    @Column(name = "role_id")
    var roleId: UUID = UUID(0L, 0L)

) : Serializable