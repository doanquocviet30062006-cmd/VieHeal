package com.clinic.platform.modules.patient.api.dto

import com.clinic.platform.modules.patient.domain.Patient
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class PatientResponse(
    val id: UUID,
    val organizationId: UUID,
    val managingFacilityId: UUID,
    val patientCode: String,
    val fullName: String,
    val dateOfBirth: LocalDate?,
    val sex: String,
    val phone: String?,
    val email: String?,
    val addressLine: String?,
    val ward: String?,
    val district: String?,
    val province: String?,
    val countryCode: String,
    val status: String,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun from(
            patient: Patient
        ): PatientResponse =
            PatientResponse(
                id = patient.id,
                organizationId = patient.organizationId,
                managingFacilityId = patient.managingFacilityId,
                patientCode = patient.patientCode,
                fullName = patient.fullName,
                dateOfBirth = patient.dateOfBirth,
                sex = patient.sex.name,
                phone = patient.phone,
                email = patient.email,
                addressLine = patient.addressLine,
                ward = patient.ward,
                district = patient.district,
                province = patient.province,
                countryCode = patient.countryCode,
                status = patient.status.name,
                createdAt = patient.createdAt,
                updatedAt = patient.updatedAt
            )
    }
}