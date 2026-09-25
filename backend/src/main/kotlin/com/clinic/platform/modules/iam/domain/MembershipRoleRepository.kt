package com.clinic.platform.modules.iam.domain

import java.util.UUID

interface MembershipRoleRepository {

    fun assign(
        membershipId: UUID,
        roleId: UUID
    )

    fun exists(
        membershipId: UUID,
        roleId: UUID
    ): Boolean

    fun findRoleIdsByMembershipId(
        membershipId: UUID
    ): List<UUID>
}