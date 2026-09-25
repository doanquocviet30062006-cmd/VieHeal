package com.clinic.platform.modules.appointment.api.dto

import jakarta.validation.constraints.Size

data class UpdateAppointmentReasonRequest(

    @field:Size(max = 1000)
    val reason: String?
)