package com.clinic.platform.modules.iam.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FacilityAssignmentJpaRepository :
    JpaRepository<FacilityAssignmentJpaEntity, UUID> {

    fun findAllByMembershipId(
        membershipId: UUID
    ): List<FacilityAssignmentJpaEntity>

    fun existsByMembershipIdAndFacilityId(
        membershipId: UUID,
        facilityId: UUID
    ): Boolean
}