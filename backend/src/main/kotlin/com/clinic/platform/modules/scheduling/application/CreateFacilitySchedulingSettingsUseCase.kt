package com.clinic.platform.modules.scheduling.application

import com.clinic.platform.modules.organization.domain.FacilityRepository
import com.clinic.platform.modules.organization.domain.FacilityStatus
import com.clinic.platform.modules.organization.domain.OrganizationRepository
import com.clinic.platform.modules.organization.domain.OrganizationStatus
import com.clinic.platform.modules.scheduling.domain.FacilitySchedulingSettings
import com.clinic.platform.modules.scheduling.domain.FacilitySchedulingSettingsRepository
import com.clinic.platform.shared.errors.ResourceAlreadyExistsException
import com.clinic.platform.shared.errors.ResourceNotFoundException
import com.clinic.platform.shared.errors.ResourceStateConflictException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CreateFacilitySchedulingSettingsUseCase(
    private val organizationRepository: OrganizationRepository,
    private val facilityRepository: FacilityRepository,
    private val facilitySchedulingSettingsRepository:
        FacilitySchedulingSettingsRepository
) {

    @Transactional
    fun execute(
        organizationId: UUID,
        facilityId: UUID,
        timeZoneId: String,
        actorUserId: UUID
    ): FacilitySchedulingSettings {

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

        val facility =
            facilityRepository.findById(facilityId)
                ?: throw ResourceNotFoundException(
                    "Facility not found in organization: $facilityId"
                )

        if (facility.organizationId != organizationId) {
            throw ResourceNotFoundException(
                "Facility not found in organization: $facilityId"
            )
        }

        if (facility.status != FacilityStatus.ACTIVE) {
            throw ResourceStateConflictException(
                message =
                    "Facility is not active for this operation",
                code =
                    "FACILITY_NOT_ACTIVE",
                currentState =
                    facility.status.name
            )
        }

        if (
            facilitySchedulingSettingsRepository
                .existsByOrganizationIdAndFacilityId(
                    organizationId,
                    facilityId
                )
        ) {
            throw ResourceAlreadyExistsException(
                "Scheduling settings already exist for facility: $facilityId"
            )
        }

        val settings =
            FacilitySchedulingSettings.create(
                organizationId = organizationId,
                facilityId = facilityId,
                timeZoneId = timeZoneId,
                actorUserId = actorUserId
            )

        return facilitySchedulingSettingsRepository.save(
            settings
        )
    }
}