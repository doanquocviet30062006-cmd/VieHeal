package com.clinic.platform.modules.encounter.application

import com.clinic.platform.modules.encounter.domain.ClinicalNote
import com.clinic.platform.modules.encounter.domain.ClinicalNoteRepository
import com.clinic.platform.modules.encounter.domain.EncounterRepository
import com.clinic.platform.modules.encounter.domain.EncounterStatus
import com.clinic.platform.shared.errors.ResourceNotFoundException
import com.clinic.platform.shared.errors.ResourceStateConflictException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UpdateClinicalNoteUseCase(
    private val encounterRepository:
        EncounterRepository,
    private val clinicalNoteRepository:
        ClinicalNoteRepository
) {

    @Transactional
    fun execute(
        organizationId: UUID,
        facilityId: UUID,
        encounterId: UUID,
        chiefComplaint: String?,
        subjective: String?,
        objective: String?,
        assessment: String?,
        plan: String?,
        actorUserId: UUID
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

        if (
            encounter.status !=
            EncounterStatus.IN_PROGRESS
        ) {
            throw ResourceStateConflictException(
                message =
                    "Clinical note cannot be updated after the encounter becomes terminal",
                code =
                    "ENCOUNTER_CLINICAL_NOTE_IMMUTABLE",
                currentState =
                    encounter.status.name
            )
        }

        val clinicalNote =
            clinicalNoteRepository
                .findByOrganizationIdAndEncounterId(
                    organizationId =
                        organizationId,
                    encounterId =
                        encounterId
                )
                ?: throw ResourceNotFoundException(
                    "Clinical note not found for encounter: $encounterId"
                )

        val updatedClinicalNote =
            clinicalNote.update(
                chiefComplaint =
                    chiefComplaint,
                subjective =
                    subjective,
                objective =
                    objective,
                assessment =
                    assessment,
                plan =
                    plan,
                actorUserId =
                    actorUserId
            )

        return clinicalNoteRepository.save(
            updatedClinicalNote
        )
    }
}