package com.clinic.platform.modules.scheduling.application

import com.clinic.platform.modules.scheduling.domain.FacilitySchedulingSettings
import com.clinic.platform.modules.scheduling.domain.FacilitySchedulingSettingsRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class GetFacilitySchedulingSettingsUseCase(
    private val facilitySchedulingSettingsRepository:
        FacilitySchedulingSettingsRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID,
        facilityId: UUID
    ): FacilitySchedulingSettings =
        facilitySchedulingSettingsRepository
            .findByOrganizationIdAndFacilityId(
                organizationId,
                facilityId
            )
            ?: throw ResourceNotFoundException(
                "Scheduling settings not found for facility: $facilityId"
            )
}