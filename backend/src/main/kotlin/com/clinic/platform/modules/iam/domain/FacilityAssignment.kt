package com.clinic.platform.modules.iam.domain

import java.time.Instant
import java.util.UUID

data class FacilityAssignment(
    val id: UUID,
    val membershipId: UUID,
    val facilityId: UUID,
    val status: FacilityAssignmentStatus,
    val assignedAt: Instant,
    val endedAt: Instant?
) {

    companion object {

        fun create(
            membershipId: UUID,
            facilityId: UUID
        ): FacilityAssignment {

            val now = Instant.now()

            return FacilityAssignment(
                id = UUID.randomUUID(),
                membershipId = membershipId,
                facilityId = facilityId,
                status = FacilityAssignmentStatus.ACTIVE,
                assignedAt = now,
                endedAt = null
            )
        }
    }
}