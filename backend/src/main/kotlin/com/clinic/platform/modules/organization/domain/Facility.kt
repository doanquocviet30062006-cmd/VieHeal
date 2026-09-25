package com.clinic.platform.modules.organization.domain

import java.time.Instant
import java.util.UUID

data class Facility(
    val id: UUID,
    val organizationId: UUID,
    val code: String,
    val name: String,
    val addressLine: String?,
    val ward: String?,
    val district: String?,
    val province: String?,
    val countryCode: String,
    val phone: String?,
    val email: String?,
    val status: FacilityStatus,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun create(
            organizationId: UUID,
            code: String,
            name: String,
            addressLine: String?,
            ward: String?,
            district: String?,
            province: String?,
            countryCode: String?,
            phone: String?,
            email: String?
        ): Facility {

            val normalizedCode = code.trim().uppercase()
            val normalizedName = name.trim()

            require(normalizedCode.isNotBlank()) {
                "Facility code must not be blank"
            }

            require(normalizedName.isNotBlank()) {
                "Facility name must not be blank"
            }

            val now = Instant.now()

            return Facility(
                id = UUID.randomUUID(),
                organizationId = organizationId,
                code = normalizedCode,
                name = normalizedName,
                addressLine = addressLine?.trim()?.takeIf { it.isNotBlank() },
                ward = ward?.trim()?.takeIf { it.isNotBlank() },
                district = district?.trim()?.takeIf { it.isNotBlank() },
                province = province?.trim()?.takeIf { it.isNotBlank() },
                countryCode = countryCode
                    ?.trim()
                    ?.uppercase()
                    ?.takeIf { it.isNotBlank() }
                    ?: "VN",
                phone = phone?.trim()?.takeIf { it.isNotBlank() },
                email = email?.trim()?.lowercase()?.takeIf { it.isNotBlank() },
                status = FacilityStatus.ACTIVE,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}