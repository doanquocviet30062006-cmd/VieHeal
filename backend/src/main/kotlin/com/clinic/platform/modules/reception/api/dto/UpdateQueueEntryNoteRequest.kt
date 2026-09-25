package com.clinic.platform.modules.reception.api.dto

import jakarta.validation.constraints.Size

data class UpdateQueueEntryNoteRequest(
    @field:Size(max = 1000)
    val note: String? = null
)