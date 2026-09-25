package com.clinic.platform.modules.patient.application

import com.clinic.platform.modules.organization.domain.FacilityRepository
import com.clinic.platform.modules.organization.domain.FacilityStatus
import com.clinic.platform.modules.organization.domain.OrganizationRepository
import com.clinic.platform.modules.organization.domain.OrganizationStatus
import com.clinic.platform.modules.patient.domain.Patient
import com.clinic.platform.modules.patient.domain.PatientRepository
import com.clinic.platform.modules.patient.domain.PatientSex
import com.clinic.platform.shared.errors.ResourceNotFoundException
import com.clinic.platform.shared.errors.ResourceStateConflictException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

@Service
class UpdatePatientUseCase(
    private val organizationRepository: OrganizationRepository,
    private val facilityRepository: FacilityRepository,
    private val patientRepository: PatientRepository
) {

    @Transactional
    fun execute(
        organizationId: UUID,
        managingFacilityId: UUID,
        patientId: UUID,
        fullName: String,
        dateOfBirth: LocalDate?,
        sex: PatientSex,
        phone: String?,
        email: String?,
        addressLine: String?,
        ward: String?,
        district: String?,
        province: String?,
        countryCode: String,
        actorUserId: UUID
    ): Patient {

        val organization =
            organizationRepository.findById(organizationId)
                ?: throw ResourceNotFoundException(
                    "Organization not found: $organizationId"
                )

        if (organization.status != OrganizationStatus.ACTIVE) {
            throw ResourceStateConflictException(
                message =
                    "Organization is not active for this operation",
                code =
                    "ORGANIZATION_NOT_ACTIVE",
                currentState =
                    organization.status.name
            )
        }

        val facility =
            facilityRepository.findById(managingFacilityId)
                ?: throw ResourceNotFoundException(
                    "Facility not found in organization: $managingFacilityId"
                )

        if (facility.organizationId != organizationId) {
            throw ResourceNotFoundException(
                "Facility not found in organization: $managingFacilityId"
            )
        }

        if (facility.status != FacilityStatus.ACTIVE) {
            throw ResourceStateConflictException(
                message =
                    "Facility is not active for this operation",
                code =
                    "FACILITY_NOT_ACTIVE",
                currentState =
                    facility.status.name
            )
        }

        val patient =
            patientRepository.findById(patientId)
                ?: throw ResourceNotFoundException(
                    "Patient not found: $patientId"
                )

        if (
            patient.organizationId != organizationId ||
            patient.managingFacilityId != managingFacilityId
        ) {
            throw ResourceNotFoundException(
                "Patient not found: $patientId"
            )
        }

        val updatedPatient =
            patient.updateProfile(
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
                actorUserId = actorUserId
            )

        return patientRepository.save(updatedPatient)
    }
}