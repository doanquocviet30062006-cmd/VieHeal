package com.clinic.platform.modules.servicecatalog.application

import com.clinic.platform.modules.organization.domain.FacilityRepository
import com.clinic.platform.modules.organization.domain.FacilityStatus
import com.clinic.platform.modules.organization.domain.OrganizationRepository
import com.clinic.platform.modules.organization.domain.OrganizationStatus
import com.clinic.platform.modules.servicecatalog.domain.FacilityService
import com.clinic.platform.modules.servicecatalog.domain.FacilityServiceRepository
import com.clinic.platform.modules.servicecatalog.domain.MedicalServiceRepository
import com.clinic.platform.modules.servicecatalog.domain.ServiceStatus
import com.clinic.platform.shared.errors.ResourceNotFoundException
import com.clinic.platform.shared.errors.ResourceStateConflictException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Service
class UpdateFacilityServiceUseCase(
    private val organizationRepository: OrganizationRepository,
    private val facilityRepository: FacilityRepository,
    private val medicalServiceRepository: MedicalServiceRepository,
    private val facilityServiceRepository: FacilityServiceRepository
) {

    @Transactional
    fun execute(
        organizationId: UUID,
        facilityId: UUID,
        facilityServiceId: UUID,
        durationMinutes: Int,
        priceAmount: BigDecimal,
        currencyCode: String,
        bookingEnabled: Boolean,
        actorUserId: UUID
    ): FacilityService {

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

        val facilityService =
            facilityServiceRepository.findById(facilityServiceId)
                ?: throw ResourceNotFoundException(
                    "Facility service not found: $facilityServiceId"
                )

        if (
            facilityService.organizationId != organizationId ||
            facilityService.facilityId != facilityId
        ) {
            throw ResourceNotFoundException(
                "Facility service not found: $facilityServiceId"
            )
        }

        val medicalService =
            medicalServiceRepository.findById(
                facilityService.serviceId
            )
                ?: throw ResourceNotFoundException(
                    "Service not found in organization: ${facilityService.serviceId}"
                )

        if (medicalService.organizationId != organizationId) {
            throw ResourceNotFoundException(
                "Service not found in organization: ${facilityService.serviceId}"
            )
        }

        if (medicalService.status != ServiceStatus.ACTIVE) {
            throw ResourceStateConflictException(
                message =
                    "Service is not active for this operation",
                code =
                    "SERVICE_NOT_ACTIVE",
                currentState =
                    medicalService.status.name
            )
        }

        val updatedFacilityService =
            facilityService.updateConfiguration(
                durationMinutes = durationMinutes,
                priceAmount = priceAmount,
                currencyCode = currencyCode,
                bookingEnabled = bookingEnabled,
                actorUserId = actorUserId
            )

        return facilityServiceRepository.save(
            updatedFacilityService
        )
    }
}