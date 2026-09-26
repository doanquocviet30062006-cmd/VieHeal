package com.clinic.platform.modules.encounter.api.dto

import java.util.UUID

data class StartEncounterRequest(
    val queueEntryId: UUID
)