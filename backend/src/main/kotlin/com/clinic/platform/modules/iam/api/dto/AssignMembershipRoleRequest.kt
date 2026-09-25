package com.clinic.platform.modules.iam.api.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AssignMembershipRoleRequest(

    @field:NotBlank
    @field:Size(min = 2, max = 100)
    val roleCode: String
)