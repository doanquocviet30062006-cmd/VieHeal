package com.clinic.platform.modules.encounter.api.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CancelEncounterRequest(

    @field:NotBlank
    @field:Size(max = 500)
    val cancellationReason: String
)