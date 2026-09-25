package com.clinic.platform.modules.servicecatalog.application

import com.clinic.platform.modules.servicecatalog.domain.FacilityService
import com.clinic.platform.modules.servicecatalog.domain.FacilityServiceRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class GetFacilityServiceUseCase(
    private val facilityServiceRepository: FacilityServiceRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID,
        facilityId: UUID,
        facilityServiceId: UUID
    ): FacilityService {

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

        return facilityService
    }
}