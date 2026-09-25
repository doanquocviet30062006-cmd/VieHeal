package com.clinic.platform.modules.appointment.api.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CancelAppointmentRequest(

    @field:NotBlank
    @field:Size(max = 500)
    val cancellationReason: String
)