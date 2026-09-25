package com.clinic.platform.modules.iam.infrastructure.persistence

import com.clinic.platform.modules.iam.domain.FacilityAssignment
import com.clinic.platform.modules.iam.domain.FacilityAssignmentRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class FacilityAssignmentRepositoryAdapter(
    private val jpaRepository: FacilityAssignmentJpaRepository
) : FacilityAssignmentRepository {

    override fun save(
        assignment: FacilityAssignment
    ): FacilityAssignment {

        return jpaRepository
            .save(
                FacilityAssignmentJpaEntity.fromDomain(
                    assignment
                )
            )
            .toDomain()
    }

    override fun findById(
        id: UUID
    ): FacilityAssignment? {

        return jpaRepository
            .findById(id)
            .orElse(null)
            ?.toDomain()
    }

    override fun findAllByMembershipId(
        membershipId: UUID
    ): List<FacilityAssignment> {

        return jpaRepository
            .findAllByMembershipId(membershipId)
            .map { it.toDomain() }
    }

    override fun existsByMembershipIdAndFacilityId(
        membershipId: UUID,
        facilityId: UUID
    ): Boolean {

        return jpaRepository
            .existsByMembershipIdAndFacilityId(
                membershipId,
                facilityId
            )
    }
}