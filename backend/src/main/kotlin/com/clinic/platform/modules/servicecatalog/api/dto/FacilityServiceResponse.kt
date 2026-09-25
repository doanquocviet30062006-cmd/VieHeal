package com.clinic.platform.modules.servicecatalog.api.dto

import com.clinic.platform.modules.servicecatalog.domain.FacilityService
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class FacilityServiceResponse(
    val id: UUID,
    val organizationId: UUID,
    val facilityId: UUID,
    val serviceId: UUID,
    val durationMinutes: Int,
    val priceAmount: BigDecimal,
    val currencyCode: String,
    val bookingEnabled: Boolean,
    val status: String,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun from(
            facilityService: FacilityService
        ): FacilityServiceResponse =
            FacilityServiceResponse(
                id = facilityService.id,
                organizationId =
                    facilityService.organizationId,
                facilityId =
                    facilityService.facilityId,
                serviceId =
                    facilityService.serviceId,
                durationMinutes =
                    facilityService.durationMinutes,
                priceAmount =
                    facilityService.priceAmount,
                currencyCode =
                    facilityService.currencyCode,
                bookingEnabled =
                    facilityService.bookingEnabled,
                status =
                    facilityService.status.name,
                createdAt =
                    facilityService.createdAt,
                updatedAt =
                    facilityService.updatedAt
            )
    }
}