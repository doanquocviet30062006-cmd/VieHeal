package com.clinic.platform.modules.servicecatalog.application

import com.clinic.platform.modules.organization.domain.OrganizationRepository
import com.clinic.platform.modules.organization.domain.OrganizationStatus
import com.clinic.platform.modules.servicecatalog.domain.MedicalService
import com.clinic.platform.modules.servicecatalog.domain.MedicalServiceRepository
import com.clinic.platform.shared.errors.ResourceAlreadyExistsException
import com.clinic.platform.shared.errors.ResourceNotFoundException
import com.clinic.platform.shared.errors.ResourceStateConflictException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CreateMedicalServiceUseCase(
    private val organizationRepository: OrganizationRepository,
    private val medicalServiceRepository: MedicalServiceRepository
) {

    @Transactional
    fun execute(
        organizationId: UUID,
        serviceCode: String,
        name: String,
        description: String?,
        category: String?,
        defaultDurationMinutes: Int,
        actorUserId: UUID
    ): MedicalService {

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

        val normalizedServiceCode =
            serviceCode
                .trim()
                .uppercase()

        if (
            medicalServiceRepository
                .existsByOrganizationIdAndServiceCode(
                    organizationId,
                    normalizedServiceCode
                )
        ) {
            throw ResourceAlreadyExistsException(
                "Service code already exists in organization: $normalizedServiceCode"
            )
        }

        val medicalService =
            MedicalService.create(
                organizationId = organizationId,
                serviceCode = normalizedServiceCode,
                name = name,
                description = description,
                category = category,
                defaultDurationMinutes = defaultDurationMinutes,
                actorUserId = actorUserId
            )

        return medicalServiceRepository.save(
            medicalService
        )
    }
}