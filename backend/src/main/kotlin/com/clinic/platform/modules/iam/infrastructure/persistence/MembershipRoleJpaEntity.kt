package com.clinic.platform.modules.iam.infrastructure.persistence

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(
    name = "membership_roles",
    schema = "iam"
)
class MembershipRoleJpaEntity(

    @EmbeddedId
    var id: MembershipRoleId

) {

    protected constructor() : this(
        MembershipRoleId()
    )
}