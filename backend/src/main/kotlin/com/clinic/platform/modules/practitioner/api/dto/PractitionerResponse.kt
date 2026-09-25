package com.clinic.platform.modules.practitioner.api.dto

import com.clinic.platform.modules.practitioner.domain.Practitioner
import java.time.Instant
import java.util.UUID

data class PractitionerResponse(
    val id: UUID,
    val organizationId: UUID,
    val membershipId: UUID,
    val practitionerCode: String,
    val fullName: String,
    val practitionerType: String,
    val licenseNumber: String?,
    val specialty: String?,
    val phone: String?,
    val email: String?,
    val status: String,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun from(
            practitioner: Practitioner
        ): PractitionerResponse =
            PractitionerResponse(
                id = practitioner.id,
                organizationId = practitioner.organizationId,
                membershipId = practitioner.membershipId,
                practitionerCode = practitioner.practitionerCode,
                fullName = practitioner.fullName,
                practitionerType = practitioner.practitionerType.name,
                licenseNumber = practitioner.licenseNumber,
                specialty = practitioner.specialty,
                phone = practitioner.phone,
                email = practitioner.email,
                status = practitioner.status.name,
                createdAt = practitioner.createdAt,
                updatedAt = practitioner.updatedAt
            )
    }
}