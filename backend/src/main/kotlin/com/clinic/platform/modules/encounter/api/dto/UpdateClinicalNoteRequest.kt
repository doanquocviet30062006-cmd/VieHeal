package com.clinic.platform.modules.encounter.api.dto

import jakarta.validation.constraints.Size

data class UpdateClinicalNoteRequest(

    @field:Size(max = 2000)
    val chiefComplaint: String?,

    @field:Size(max = 20000)
    val subjective: String?,

    @field:Size(max = 20000)
    val objective: String?,

    @field:Size(max = 20000)
    val assessment: String?,

    @field:Size(max = 20000)
    val plan: String?
)