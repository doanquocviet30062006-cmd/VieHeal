package com.clinic.platform.modules.iam.api.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateOrganizationUserRequest(

    @field:Email
    @field:Size(max = 255)
    val email: String? = null,

    @field:Size(max = 30)
    val phone: String? = null,

    @field:NotBlank
    @field:Size(min = 2, max = 255)
    val displayName: String
)