package com.clinic.platform.modules.encounter.application

import com.clinic.platform.modules.encounter.domain.ClinicalNote
import com.clinic.platform.modules.encounter.domain.ClinicalNoteRepository
import com.clinic.platform.modules.encounter.domain.EncounterRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class GetClinicalNoteUseCase(
    private val encounterRepository:
        EncounterRepository,
    private val clinicalNoteRepository:
        ClinicalNoteRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID,
        facilityId: UUID,
        encounterId: UUID
    ): ClinicalNote {

        val encounter =
            encounterRepository.findById(
                encounterId
            )
                ?: throw ResourceNotFoundException(
                    "Encounter not found: $encounterId"
                )

        if (
            encounter.organizationId != organizationId ||
            encounter.facilityId != facilityId
        ) {
            throw ResourceNotFoundException(
                "Encounter not found: $encounterId"
            )
        }

        return clinicalNoteRepository
            .findByOrganizationIdAndEncounterId(
                organizationId =
                    organizationId,
                encounterId =
                    encounterId
            )
            ?: throw ResourceNotFoundException(
                "Clinical note not found for encounter: $encounterId"
            )
    }
}