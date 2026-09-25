package com.clinic.platform.modules.iam.api.dto

import jakarta.validation.constraints.NotNull
import java.util.UUID

data class AssignFacilityRequest(

    @field:NotNull
    val facilityId: UUID? = null
)