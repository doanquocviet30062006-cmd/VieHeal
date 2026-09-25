package com.clinic.platform.modules.patient.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PatientJpaRepository :
    JpaRepository<PatientJpaEntity, UUID> {

    fun findByOrganizationIdAndPatientCode(
        organizationId: UUID,
        patientCode: String
    ): PatientJpaEntity?

    fun existsByOrganizationIdAndPatientCode(
        organizationId: UUID,
        patientCode: String
    ): Boolean

    fun findAllByOrganizationIdAndManagingFacilityId(
        organizationId: UUID,
        managingFacilityId: UUID
    ): List<PatientJpaEntity>
}
