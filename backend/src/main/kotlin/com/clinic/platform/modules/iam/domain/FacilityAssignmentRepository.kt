package com.clinic.platform.modules.iam.domain

import java.util.UUID

interface FacilityAssignmentRepository {

    fun save(
        assignment: FacilityAssignment
    ): FacilityAssignment

    fun findById(
        id: UUID
    ): FacilityAssignment?

    fun findAllByMembershipId(
        membershipId: UUID
    ): List<FacilityAssignment>

    fun existsByMembershipIdAndFacilityId(
        membershipId: UUID,
        facilityId: UUID
    ): Boolean
}