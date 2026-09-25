package com.clinic.platform.modules.practitioner.application

import com.clinic.platform.modules.iam.domain.MembershipStatus
import com.clinic.platform.modules.iam.domain.OrganizationMembershipRepository
import com.clinic.platform.modules.organization.domain.OrganizationRepository
import com.clinic.platform.modules.organization.domain.OrganizationStatus
import com.clinic.platform.modules.practitioner.domain.Practitioner
import com.clinic.platform.modules.practitioner.domain.PractitionerRepository
import com.clinic.platform.modules.practitioner.domain.PractitionerType
import com.clinic.platform.shared.errors.ResourceNotFoundException
import com.clinic.platform.shared.errors.ResourceStateConflictException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UpdatePractitionerUseCase(
    private val organizationRepository: OrganizationRepository,
    private val organizationMembershipRepository: OrganizationMembershipRepository,
    private val practitionerRepository: PractitionerRepository
) {

    @Transactional
    fun execute(
        organizationId: UUID,
        practitionerId: UUID,
        fullName: String,
        practitionerType: PractitionerType,
        licenseNumber: String?,
        specialty: String?,
        phone: String?,
        email: String?,
        actorUserId: UUID
    ): Practitioner {

        val organization =
            organizationRepository.findById(organizationId)
                ?: throw ResourceNotFoundException(
                    "Organization not found: $organizationId"
                )

        if (organization.status != OrganizationStatus.ACTIVE) {
            throw ResourceStateConflictException(
                message =
                    "Organization is not active for this operation",
                code =
                    "ORGANIZATION_NOT_ACTIVE",
                currentState =
                    organization.status.name
            )
        }

        val practitioner =
            practitionerRepository.findById(practitionerId)
                ?: throw ResourceNotFoundException(
                    "Practitioner not found: $practitionerId"
                )

        if (practitioner.organizationId != organizationId) {
            throw ResourceNotFoundException(
                "Practitioner not found: $practitionerId"
            )
        }

        val membership =
            organizationMembershipRepository
                .findById(practitioner.membershipId)
                ?: throw ResourceNotFoundException(
                    "Membership not found in organization: ${practitioner.membershipId}"
                )

        if (membership.organizationId != organizationId) {
            throw ResourceNotFoundException(
                "Membership not found in organization: ${practitioner.membershipId}"
            )
        }

        if (membership.status != MembershipStatus.ACTIVE) {
            throw ResourceStateConflictException(
                message =
                    "Membership is not active for this operation",
                code =
                    "MEMBERSHIP_NOT_ACTIVE",
                currentState =
                    membership.status.name
            )
        }

        val updatedPractitioner =
            practitioner.updateProfile(
                fullName = fullName,
                practitionerType = practitionerType,
                licenseNumber = licenseNumber,
                specialty = specialty,
                phone = phone,
                email = email,
                actorUserId = actorUserId
            )

        return practitionerRepository.save(
            updatedPractitioner
        )
    }
}