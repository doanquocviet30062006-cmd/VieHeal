package com.clinic.platform.modules.scheduling.api.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.time.LocalDate
import java.time.LocalTime

data class CreatePractitionerAvailabilityRuleRequest(

    @field:Min(1)
    @field:Max(7)
    val dayOfWeek: Int,

    val startLocalTime: LocalTime,

    val endLocalTime: LocalTime,

    val effectiveFrom: LocalDate,

    val effectiveTo: LocalDate?
)