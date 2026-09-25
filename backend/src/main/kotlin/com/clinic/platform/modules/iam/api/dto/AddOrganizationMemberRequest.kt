package com.clinic.platform.modules.iam.api.dto

import jakarta.validation.constraints.NotNull
import java.util.UUID

data class AddOrganizationMemberRequest(

    @field:NotNull
    val userId: UUID? = null
)