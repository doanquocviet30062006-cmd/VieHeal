package com.clinic.platform.modules.servicecatalog.api.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class UpdateMedicalServiceRequest(

    @field:NotBlank
    @field:Size(max = 255)
    val name: String,

    @field:Size(max = 1000)
    val description: String?,

    @field:Size(max = 100)
    val category: String?,

    @field:Positive
    val defaultDurationMinutes: Int
)