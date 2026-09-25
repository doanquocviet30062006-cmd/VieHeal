package com.clinic.platform.modules.servicecatalog.domain

import com.clinic.platform.shared.errors.DomainValidationException
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class FacilityService(
    val id: UUID,
    val organizationId: UUID,
    val facilityId: UUID,
    val serviceId: UUID,
    val durationMinutes: Int,
    val priceAmount: BigDecimal,
    val currencyCode: String,
    val bookingEnabled: Boolean,
    val status: FacilityServiceStatus,
    val createdByUserId: UUID,
    val updatedByUserId: UUID,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun create(
            organizationId: UUID,
            facilityId: UUID,
            serviceId: UUID,
            durationMinutes: Int,
            priceAmount: BigDecimal,
            currencyCode: String,
            bookingEnabled: Boolean,
            actorUserId: UUID
        ): FacilityService {

            val normalizedCurrencyCode =
                currencyCode
                    .trim()
                    .uppercase()

            validateDuration(
                durationMinutes
            )

            validatePrice(
                priceAmount
            )

            validateCurrencyCode(
                normalizedCurrencyCode
            )

            val now = Instant.now()

            return FacilityService(
                id = UUID.randomUUID(),
                organizationId = organizationId,
                facilityId = facilityId,
                serviceId = serviceId,
                durationMinutes = durationMinutes,
                priceAmount = priceAmount,
                currencyCode = normalizedCurrencyCode,
                bookingEnabled = bookingEnabled,
                status = FacilityServiceStatus.ACTIVE,
                createdByUserId = actorUserId,
                updatedByUserId = actorUserId,
                createdAt = now,
                updatedAt = now
            )
        }

        private fun validateDuration(
            durationMinutes: Int
        ) {
            if (durationMinutes <= 0) {
                throw DomainValidationException(
                    message =
                        "Facility service duration must be greater than zero",
                    code =
                        "FACILITY_SERVICE_DURATION_INVALID"
                )
            }
        }

        private fun validatePrice(
            priceAmount: BigDecimal
        ) {
            if (priceAmount < BigDecimal.ZERO) {
                throw DomainValidationException(
                    message =
                        "Facility service price must not be negative",
                    code =
                        "FACILITY_SERVICE_PRICE_INVALID"
                )
            }
        }

        private fun validateCurrencyCode(
            currencyCode: String
        ) {
            if (
                !currencyCode.matches(
                    Regex("^[A-Z]{3}$")
                )
            ) {
                throw DomainValidationException(
                    message =
                        "Facility service currency code must contain exactly 3 uppercase letters",
                    code =
                        "FACILITY_SERVICE_CURRENCY_INVALID"
                )
            }
        }
    }

    fun updateConfiguration(
        durationMinutes: Int,
        priceAmount: BigDecimal,
        currencyCode: String,
        bookingEnabled: Boolean,
        actorUserId: UUID
    ): FacilityService {

        val normalizedCurrencyCode =
            currencyCode
                .trim()
                .uppercase()

        validateDuration(
            durationMinutes
        )

        validatePrice(
            priceAmount
        )

        validateCurrencyCode(
            normalizedCurrencyCode
        )

        return copy(
            durationMinutes = durationMinutes,
            priceAmount = priceAmount,
            currencyCode = normalizedCurrencyCode,
            bookingEnabled = bookingEnabled,
            updatedByUserId = actorUserId,
            updatedAt = Instant.now()
        )
    }
}