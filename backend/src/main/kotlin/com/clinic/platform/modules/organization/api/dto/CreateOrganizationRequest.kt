package com.clinic.platform.modules.organization.api.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateOrganizationRequest(

    @field:NotBlank
    @field:Size(
        min = 2,
        max = 50
    )
    val code: String,

    @field:NotBlank
    @field:Size(
        min = 2,
        max = 255
    )
    val name: String
)