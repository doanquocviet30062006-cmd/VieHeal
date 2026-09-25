package com.clinic.platform.modules.scheduling.api.dto

import com.clinic.platform.modules.scheduling.domain.FacilitySchedulingSettings
import java.time.Instant
import java.util.UUID

data class FacilitySchedulingSettingsResponse(
    val id: UUID,
    val organizationId: UUID,
    val facilityId: UUID,
    val timeZoneId: String,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun from(
            settings: FacilitySchedulingSettings
        ): FacilitySchedulingSettingsResponse =
            FacilitySchedulingSettingsResponse(
                id = settings.id,
                organizationId = settings.organizationId,
                facilityId = settings.facilityId,
                timeZoneId = settings.timeZoneId,
                createdAt = settings.createdAt,
                updatedAt = settings.updatedAt
            )
    }
}