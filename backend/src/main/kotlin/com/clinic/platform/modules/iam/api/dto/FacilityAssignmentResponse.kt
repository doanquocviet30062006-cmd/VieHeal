package com.clinic.platform.modules.iam.api.dto

import com.clinic.platform.modules.iam.domain.FacilityAssignment
import com.clinic.platform.modules.iam.domain.FacilityAssignmentStatus
import java.time.Instant
import java.util.UUID

data class FacilityAssignmentResponse(
    val id: UUID,
    val membershipId: UUID,
    val facilityId: UUID,
    val status: FacilityAssignmentStatus,
    val assignedAt: Instant,
    val endedAt: Instant?
) {

    companion object {

        fun fromDomain(
            assignment: FacilityAssignment
        ): FacilityAssignmentResponse {

            return FacilityAssignmentResponse(
                id = assignment.id,
                membershipId = assignment.membershipId,
                facilityId = assignment.facilityId,
                status = assignment.status,
                assignedAt = assignment.assignedAt,
                endedAt = assignment.endedAt
            )
        }
    }
}