package com.clinic.platform.modules.iam.application

import com.clinic.platform.modules.iam.domain.FacilityAssignment
import com.clinic.platform.modules.iam.domain.FacilityAssignmentRepository
import com.clinic.platform.modules.iam.domain.OrganizationMembershipRepository
import com.clinic.platform.modules.organization.domain.FacilityRepository
import com.clinic.platform.shared.errors.ResourceAlreadyExistsException
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AssignFacilityUseCase(
    private val membershipRepository: OrganizationMembershipRepository,
    private val facilityRepository: FacilityRepository,
    private val facilityAssignmentRepository: FacilityAssignmentRepository
) {

    @Transactional
    fun execute(
        membershipId: UUID,
        facilityId: UUID
    ): FacilityAssignment {

        val membership =
            membershipRepository.findById(membershipId)
                ?: throw ResourceNotFoundException(
                    "Organization membership not found: $membershipId"
                )

        val facility =
            facilityRepository.findById(facilityId)
                ?: throw ResourceNotFoundException(
                    "Facility not found: $facilityId"
                )

        if (
            facility.organizationId !=
            membership.organizationId
        ) {
            throw ResourceNotFoundException(
                "Facility not found in membership organization: $facilityId"
            )
        }

        if (
            facilityAssignmentRepository
                .existsByMembershipIdAndFacilityId(
                    membershipId,
                    facilityId
                )
        ) {
            throw ResourceAlreadyExistsException(
                "Facility is already assigned to membership"
            )
        }

        val assignment =
            FacilityAssignment.create(
                membershipId = membershipId,
                facilityId = facilityId
            )

        return facilityAssignmentRepository.save(
            assignment
        )
    }
}