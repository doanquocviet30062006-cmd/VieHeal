package com.clinic.platform.modules.scheduling.api.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpsertFacilitySchedulingSettingsRequest(

    @field:NotBlank
    @field:Size(max = 100)
    val timeZoneId: String
)