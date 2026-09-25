package com.clinic.platform.modules.iam.domain

import java.time.Instant
import java.util.UUID

data class OrganizationMembership(
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

        fun create(
            userId: UUID,
            organizationId: UUID
        ): OrganizationMembership {

            val now = Instant.now()

            return OrganizationMembership(
                id = UUID.randomUUID(),
                userId = userId,
                organizationId = organizationId,
                status = MembershipStatus.ACTIVE,
                joinedAt = now,
                endedAt = null,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}