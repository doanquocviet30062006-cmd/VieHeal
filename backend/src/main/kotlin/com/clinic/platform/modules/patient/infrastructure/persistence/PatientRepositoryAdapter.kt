package com.clinic.platform.modules.patient.infrastructure.persistence

import com.clinic.platform.modules.patient.domain.Patient
import com.clinic.platform.modules.patient.domain.PatientRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class PatientRepositoryAdapter(
    private val patientJpaRepository: PatientJpaRepository
) : PatientRepository {

    override fun save(
        patient: Patient
    ): Patient {
        val entity = patient.toJpaEntity()

        return patientJpaRepository
            .save(entity)
            .toDomain()
    }

    override fun findById(
        id: UUID
    ): Patient? =
        patientJpaRepository
            .findById(id)
            .orElse(null)
            ?.toDomain()

    override fun findByOrganizationIdAndPatientCode(
        organizationId: UUID,
        patientCode: String
    ): Patient? =
        patientJpaRepository
            .findByOrganizationIdAndPatientCode(
                organizationId,
                patientCode
            )
            ?.toDomain()

    override fun existsByOrganizationIdAndPatientCode(
        organizationId: UUID,
        patientCode: String
    ): Boolean =
        patientJpaRepository
            .existsByOrganizationIdAndPatientCode(
                organizationId,
                patientCode
            )

    override fun findAllByOrganizationIdAndManagingFacilityId(
        organizationId: UUID,
        managingFacilityId: UUID
    ): List<Patient> =
        patientJpaRepository
            .findAllByOrganizationIdAndManagingFacilityId(
                organizationId,
                managingFacilityId
            )
            .map {
                it.toDomain()
            }

    private fun Patient.toJpaEntity(): PatientJpaEntity =
        PatientJpaEntity(
            id = id,
            organizationId = organizationId,
            managingFacilityId = managingFacilityId,
            patientCode = patientCode,
            fullName = fullName,
            dateOfBirth = dateOfBirth,
            sex = sex,
            phone = phone,
            email = email,
            addressLine = addressLine,
            ward = ward,
            district = district,
            province = province,
            countryCode = countryCode,
            status = status,
            createdByUserId = createdByUserId,
            updatedByUserId = updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

    private fun PatientJpaEntity.toDomain(): Patient =
        Patient(
            id = id,
            organizationId = organizationId,
            managingFacilityId = managingFacilityId,
            patientCode = patientCode,
            fullName = fullName,
            dateOfBirth = dateOfBirth,
            sex = sex,
            phone = phone,
            email = email,
            addressLine = addressLine,
            ward = ward,
            district = district,
            province = province,
            countryCode = countryCode,
            status = status,
            createdByUserId = createdByUserId,
            updatedByUserId = updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}
