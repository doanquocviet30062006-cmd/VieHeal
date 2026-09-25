package com.clinic.platform.modules.appointment.api.dto

import java.time.Instant

data class RescheduleAppointmentRequest(
    val scheduledStartAt: Instant
)