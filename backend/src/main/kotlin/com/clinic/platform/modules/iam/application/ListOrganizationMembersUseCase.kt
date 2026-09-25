package com.clinic.platform.modules.iam.application

import com.clinic.platform.modules.iam.domain.OrganizationMembership
import com.clinic.platform.modules.iam.domain.OrganizationMembershipRepository
import com.clinic.platform.modules.organization.domain.OrganizationRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ListOrganizationMembersUseCase(
    private val organizationRepository: OrganizationRepository,
    private val membershipRepository: OrganizationMembershipRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID
    ): List<OrganizationMembership> {

        organizationRepository.findById(organizationId)
            ?: throw ResourceNotFoundException(
                "Organization not found: $organizationId"
            )

        return membershipRepository
            .findAllByOrganizationId(organizationId)
    }
}