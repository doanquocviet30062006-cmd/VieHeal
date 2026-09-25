package com.clinic.platform.modules.servicecatalog.application

import com.clinic.platform.modules.organization.domain.FacilityRepository
import com.clinic.platform.modules.servicecatalog.domain.FacilityService
import com.clinic.platform.modules.servicecatalog.domain.FacilityServiceRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ListFacilityServicesUseCase(
    private val facilityRepository: FacilityRepository,
    private val facilityServiceRepository: FacilityServiceRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID,
        facilityId: UUID
    ): List<FacilityService> {

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

        return facilityServiceRepository
            .findAllByOrganizationIdAndFacilityId(
                organizationId,
                facilityId
            )
    }
}