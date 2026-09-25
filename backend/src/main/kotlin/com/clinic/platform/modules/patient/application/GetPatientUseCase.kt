package com.clinic.platform.modules.patient.application

import com.clinic.platform.modules.patient.domain.Patient
import com.clinic.platform.modules.patient.domain.PatientRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class GetPatientUseCase(
    private val patientRepository: PatientRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID,
        managingFacilityId: UUID,
        patientId: UUID
    ): Patient {

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

        return patient
    }
}