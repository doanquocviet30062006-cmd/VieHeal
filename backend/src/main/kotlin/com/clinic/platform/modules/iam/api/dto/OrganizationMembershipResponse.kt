package com.clinic.platform.modules.iam.api.dto

import com.clinic.platform.modules.iam.domain.MembershipStatus
import com.clinic.platform.modules.iam.domain.OrganizationMembership
import java.time.Instant
import java.util.UUID

data class OrganizationMembershipResponse(
    val id: UUID,
    val userId: UUID,
    val organizationId: UUID,
    val status: MembershipStatus,
    val joinedAt: Instant,
    val endedAt: Instant?,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun fromDomain(
            membership: OrganizationMembership
        ): OrganizationMembershipResponse {

            return OrganizationMembershipResponse(
                id = membership.id,
                userId = membership.userId,
                organizationId = membership.organizationId,
                status = membership.status,
                joinedAt = membership.joinedAt,
                endedAt = membership.endedAt,
                createdAt = membership.createdAt,
                updatedAt = membership.updatedAt
            )
        }
    }
}