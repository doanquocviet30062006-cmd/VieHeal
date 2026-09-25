package com.clinic.platform.modules.iam.application

import com.clinic.platform.modules.iam.domain.IamUserRepository
import com.clinic.platform.modules.iam.domain.OrganizationMembership
import com.clinic.platform.modules.iam.domain.OrganizationMembershipRepository
import com.clinic.platform.modules.organization.domain.OrganizationRepository
import com.clinic.platform.shared.errors.ResourceAlreadyExistsException
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CreateOrganizationMembershipUseCase(
    private val userRepository: IamUserRepository,
    private val organizationRepository: OrganizationRepository,
    private val membershipRepository: OrganizationMembershipRepository
) {

    @Transactional
    fun execute(
        organizationId: UUID,
        userId: UUID
    ): OrganizationMembership {

        userRepository.findById(userId)
            ?: throw ResourceNotFoundException(
                "IAM user not found: $userId"
            )

        organizationRepository.findById(organizationId)
            ?: throw ResourceNotFoundException(
                "Organization not found: $organizationId"
            )

        if (
            membershipRepository.existsByUserIdAndOrganizationId(
                userId,
                organizationId
            )
        ) {
            throw ResourceAlreadyExistsException(
                "User is already a member of organization"
            )
        }

        val membership = OrganizationMembership.create(
            userId = userId,
            organizationId = organizationId
        )

        return membershipRepository.save(membership)
    }
}