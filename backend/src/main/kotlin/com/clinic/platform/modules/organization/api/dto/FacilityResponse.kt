package com.clinic.platform.modules.organization.api.dto

import com.clinic.platform.modules.organization.domain.Facility
import java.time.Instant
import java.util.UUID

data class FacilityResponse(
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
    val status: String,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun from(
            facility: Facility
        ): FacilityResponse =
            FacilityResponse(
                id = facility.id,
                organizationId = facility.organizationId,
                code = facility.code,
                name = facility.name,
                addressLine = facility.addressLine,
                ward = facility.ward,
                district = facility.district,
                province = facility.province,
                countryCode = facility.countryCode,
                phone = facility.phone,
                email = facility.email,
                status = facility.status.name,
                createdAt = facility.createdAt,
                updatedAt = facility.updatedAt
            )
    }
}