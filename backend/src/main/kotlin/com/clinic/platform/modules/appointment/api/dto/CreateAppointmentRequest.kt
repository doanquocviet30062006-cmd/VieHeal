package com.clinic.platform.modules.appointment.api.dto

import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.UUID

data class CreateAppointmentRequest(
    val patientId: UUID,
    val practitionerId: UUID,
    val facilityServiceId: UUID,
    val scheduledStartAt: Instant,

    @field:Size(max = 1000)
    val reason: String? = null
)