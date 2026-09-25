package com.clinic.platform.modules.practitioner.api.dto

import com.clinic.platform.modules.practitioner.domain.PractitionerType
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

data class CreatePractitionerRequest(

    val membershipId: UUID,

    @field:NotBlank
    @field:Size(max = 50)
    val practitionerCode: String,

    @field:NotBlank
    @field:Size(max = 255)
    val fullName: String,

    val practitionerType: PractitionerType,

    @field:Size(max = 100)
    val licenseNumber: String?,

    @field:Size(max = 150)
    val specialty: String?,

    @field:Size(max = 30)
    val phone: String?,

    @field:Email
    @field:Size(max = 255)
    val email: String?
)