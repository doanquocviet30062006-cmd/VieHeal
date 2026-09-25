package com.clinic.platform.modules.patient.application

import com.clinic.platform.modules.organization.domain.FacilityRepository
import com.clinic.platform.modules.organization.domain.FacilityStatus
import com.clinic.platform.modules.organization.domain.OrganizationRepository
import com.clinic.platform.modules.organization.domain.OrganizationStatus
import com.clinic.platform.modules.patient.domain.Patient
import com.clinic.platform.modules.patient.domain.PatientRepository
import com.clinic.platform.modules.patient.domain.PatientSex
import com.clinic.platform.shared.errors.ResourceAlreadyExistsException
import com.clinic.platform.shared.errors.ResourceNotFoundException
import com.clinic.platform.shared.errors.ResourceStateConflictException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

@Service
class CreatePatientUseCase(
    private val organizationRepository: OrganizationRepository,
    private val facilityRepository: FacilityRepository,
    private val patientRepository: PatientRepository
) {

    @Transactional
    fun execute(
        organizationId: UUID,
        managingFacilityId: UUID,
        patientCode: String,
        fullName: String,
        dateOfBirth: LocalDate?,
        sex: PatientSex,
        phone: String?,
        email: String?,
        addressLine: String?,
        ward: String?,
        district: String?,
        province: String?,
        countryCode: String?,
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

        val normalizedPatientCode =
            patientCode
                .trim()
                .uppercase()

        if (
            patientRepository
                .existsByOrganizationIdAndPatientCode(
                    organizationId,
                    normalizedPatientCode
                )
        ) {
            throw ResourceAlreadyExistsException(
                "Patient code already exists in organization: $normalizedPatientCode"
            )
        }

        val normalizedCountryCode =
            countryCode
                ?.trim()
                ?.uppercase()
                ?.takeIf { it.isNotBlank() }
                ?: "VN"

        val patient =
            Patient.create(
                organizationId = organizationId,
                managingFacilityId = managingFacilityId,
                patientCode = normalizedPatientCode,
                fullName = fullName,
                dateOfBirth = dateOfBirth,
                sex = sex,
                phone = phone,
                email = email,
                addressLine = addressLine,
                ward = ward,
                district = district,
                province = province,
                countryCode = normalizedCountryCode,
                actorUserId = actorUserId
            )

        return patientRepository.save(patient)
    }
}