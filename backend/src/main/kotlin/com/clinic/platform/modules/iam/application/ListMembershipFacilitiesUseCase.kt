package com.clinic.platform.modules.iam.application

import com.clinic.platform.modules.iam.domain.FacilityAssignment
import com.clinic.platform.modules.iam.domain.FacilityAssignmentRepository
import com.clinic.platform.modules.iam.domain.OrganizationMembershipRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ListMembershipFacilitiesUseCase(
    private val membershipRepository: OrganizationMembershipRepository,
    private val facilityAssignmentRepository: FacilityAssignmentRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        membershipId: UUID
    ): List<FacilityAssignment> {

        membershipRepository.findById(membershipId)
            ?: throw ResourceNotFoundException(
                "Organization membership not found: $membershipId"
            )

        return facilityAssignmentRepository
            .findAllByMembershipId(membershipId)
    }
}