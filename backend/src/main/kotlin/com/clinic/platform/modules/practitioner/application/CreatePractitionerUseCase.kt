package com.clinic.platform.modules.practitioner.application

import com.clinic.platform.modules.iam.domain.MembershipStatus
import com.clinic.platform.modules.iam.domain.OrganizationMembershipRepository
import com.clinic.platform.modules.organization.domain.OrganizationRepository
import com.clinic.platform.modules.organization.domain.OrganizationStatus
import com.clinic.platform.modules.practitioner.domain.Practitioner
import com.clinic.platform.modules.practitioner.domain.PractitionerRepository
import com.clinic.platform.modules.practitioner.domain.PractitionerType
import com.clinic.platform.shared.errors.ResourceAlreadyExistsException
import com.clinic.platform.shared.errors.ResourceNotFoundException
import com.clinic.platform.shared.errors.ResourceStateConflictException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CreatePractitionerUseCase(
    private val organizationRepository: OrganizationRepository,
    private val organizationMembershipRepository: OrganizationMembershipRepository,
    private val practitionerRepository: PractitionerRepository
) {

    @Transactional
    fun execute(
        organizationId: UUID,
        membershipId: UUID,
        practitionerCode: String,
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

        val membership =
            organizationMembershipRepository.findById(membershipId)
                ?: throw ResourceNotFoundException(
                    "Membership not found in organization: $membershipId"
                )

        if (membership.organizationId != organizationId) {
            throw ResourceNotFoundException(
                "Membership not found in organization: $membershipId"
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

        val normalizedPractitionerCode =
            practitionerCode
                .trim()
                .uppercase()

        if (
            practitionerRepository
                .existsByOrganizationIdAndPractitionerCode(
                    organizationId,
                    normalizedPractitionerCode
                )
        ) {
            throw ResourceAlreadyExistsException(
                "Practitioner code already exists in organization: $normalizedPractitionerCode"
            )
        }

        if (
            practitionerRepository
                .existsByOrganizationIdAndMembershipId(
                    organizationId,
                    membershipId
                )
        ) {
            throw ResourceAlreadyExistsException(
                "Practitioner profile already exists for membership: $membershipId"
            )
        }

        val practitioner =
            Practitioner.create(
                organizationId = organizationId,
                membershipId = membershipId,
                practitionerCode = normalizedPractitionerCode,
                fullName = fullName,
                practitionerType = practitionerType,
                licenseNumber = licenseNumber,
                specialty = specialty,
                phone = phone,
                email = email,
                actorUserId = actorUserId
            )

        return practitionerRepository.save(practitioner)
    }
}