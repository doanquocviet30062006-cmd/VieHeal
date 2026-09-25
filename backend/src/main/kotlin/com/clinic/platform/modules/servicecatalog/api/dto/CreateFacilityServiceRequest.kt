package com.clinic.platform.modules.servicecatalog.api.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.util.UUID

data class CreateFacilityServiceRequest(

    val serviceId: UUID,

    @field:Positive
    val durationMinutes: Int,

    @field:DecimalMin(
        value = "0.00",
        inclusive = true
    )
    @field:Digits(
        integer = 10,
        fraction = 2
    )
    val priceAmount: BigDecimal,

    @field:NotBlank
    @field:Size(
        min = 3,
        max = 3
    )
    @field:Pattern(
        regexp = "^[A-Za-z]{3}$"
    )
    val currencyCode: String = "VND",

    val bookingEnabled: Boolean = true
)