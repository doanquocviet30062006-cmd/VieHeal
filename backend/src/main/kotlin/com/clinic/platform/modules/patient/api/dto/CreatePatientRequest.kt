package com.clinic.platform.modules.patient.api.dto

import com.clinic.platform.modules.patient.domain.PatientSex
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class CreatePatientRequest(

    @field:NotBlank
    @field:Size(max = 50)
    val patientCode: String,

    @field:NotBlank
    @field:Size(max = 255)
    val fullName: String,

    val dateOfBirth: LocalDate?,

    val sex: PatientSex,

    @field:Size(max = 30)
    val phone: String?,

    @field:Email
    @field:Size(max = 255)
    val email: String?,

    @field:Size(max = 255)
    val addressLine: String?,

    @field:Size(max = 100)
    val ward: String?,

    @field:Size(max = 100)
    val district: String?,

    @field:Size(max = 100)
    val province: String?,

    @field:Size(min = 2, max = 2)
    val countryCode: String? = "VN"
)