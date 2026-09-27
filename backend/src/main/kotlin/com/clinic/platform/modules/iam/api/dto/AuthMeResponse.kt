package com.clinic.platform.modules.iam.api.dto

import java.util.UUID

data class AuthMeResponse(
    val user: AuthMeUserResponse,
    val systemRoles: Set<String>,
    val systemPermissions: Set<String>,
    val organizations: List<AuthMeOrganizationResponse>
)

data class AuthMeUserResponse(
    val id: UUID,
    val externalSubject: String,
    val displayName: String
)

data class AuthMeOrganizationResponse(
    val membershipId: UUID,
    val organizationId: UUID,
    val roles: Set<String>,
    val permissions: Set<String>,
    val facilities: List<AuthMeFacilityResponse>
)

data class AuthMeFacilityResponse(
    val facilityId: UUID
)
