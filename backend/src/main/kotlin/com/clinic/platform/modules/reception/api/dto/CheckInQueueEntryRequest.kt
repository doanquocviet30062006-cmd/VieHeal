package com.clinic.platform.modules.reception.api.dto

import jakarta.validation.constraints.Size
import java.util.UUID

data class CheckInQueueEntryRequest(
    val appointmentId: UUID,

    @field:Size(max = 1000)
    val note: String? = null
)