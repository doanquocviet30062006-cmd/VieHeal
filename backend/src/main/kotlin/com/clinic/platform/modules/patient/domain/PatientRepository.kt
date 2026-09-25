package com.clinic.platform.modules.patient.domain

import java.util.UUID

interface PatientRepository {

    fun save(
        patient: Patient
    ): Patient

    fun findById(
        id: UUID
    ): Patient?

    fun findByOrganizationIdAndPatientCode(
        organizationId: UUID,
        patientCode: String
    ): Patient?

    fun existsByOrganizationIdAndPatientCode(
        organizationId: UUID,
        patientCode: String
    ): Boolean

    fun findAllByOrganizationIdAndManagingFacilityId(
        organizationId: UUID,
        managingFacilityId: UUID
    ): List<Patient>
}