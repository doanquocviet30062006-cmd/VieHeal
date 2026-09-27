package com.clinic.platform.infrastructure.security

import com.clinic.platform.modules.iam.domain.IamUser
import java.util.UUID

data class AccessContext(
    val user: IamUser,
    val organizations: List<OrganizationAccess>,
    val systemRoles: Set<String> = emptySet(),
    val systemPermissions: Set<String> = emptySet()
)

data class OrganizationAccess(
    val membershipId: UUID,
    val organizationId: UUID,
    val roles: Set<String>,
    val permissions: Set<String>,
    val facilityIds: Set<UUID>
)