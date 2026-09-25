package com.clinic.platform.modules.patient.application

import com.clinic.platform.modules.organization.domain.FacilityRepository
import com.clinic.platform.modules.patient.domain.Patient
import com.clinic.platform.modules.patient.domain.PatientRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ListPatientsUseCase(
    private val patientRepository: PatientRepository,
    private val facilityRepository: FacilityRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID,
        managingFacilityId: UUID
    ): List<Patient> {

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

        return patientRepository
            .findAllByOrganizationIdAndManagingFacilityId(
                organizationId = organizationId,
                managingFacilityId = managingFacilityId
            )
    }
}