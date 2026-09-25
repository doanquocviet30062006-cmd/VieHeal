package com.clinic.platform.modules.scheduling.api.dto

import com.clinic.platform.modules.scheduling.domain.AvailabilityExceptionType
import jakarta.validation.constraints.Size
import java.time.Instant

data class CreatePractitionerAvailabilityExceptionRequest(
    val exceptionType: AvailabilityExceptionType,
    val startAt: Instant,
    val endAt: Instant,

    @field:Size(max = 500)
    val reason: String?
)