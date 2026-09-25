package com.clinic.platform.modules.organization.api.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateFacilityRequest(

    @field:NotBlank
    @field:Size(min = 2, max = 50)
    val code: String,

    @field:NotBlank
    @field:Size(min = 2, max = 255)
    val name: String,

    @field:Size(max = 500)
    val addressLine: String?,

    @field:Size(max = 150)
    val ward: String?,

    @field:Size(max = 150)
    val district: String?,

    @field:Size(max = 150)
    val province: String?,

    @field:Size(min = 2, max = 2)
    val countryCode: String? = "VN",

    @field:Size(max = 30)
    val phone: String?,

    @field:Email
    @field:Size(max = 255)
    val email: String?
)