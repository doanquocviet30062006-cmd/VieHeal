package com.clinic.platform.modules.encounter.api.dto

import com.clinic.platform.modules.encounter.domain.ClinicalNote
import java.time.Instant
import java.util.UUID

data class ClinicalNoteResponse(
    val id: UUID,
    val organizationId: UUID,
    val facilityId: UUID,
    val encounterId: UUID,
    val chiefComplaint: String?,
    val subjective: String?,
    val objective: String?,
    val assessment: String?,
    val plan: String?,
    val createdByUserId: UUID,
    val updatedByUserId: UUID,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun from(
            clinicalNote: ClinicalNote
        ): ClinicalNoteResponse =
            ClinicalNoteResponse(
                id =
                    clinicalNote.id,
                organizationId =
                    clinicalNote.organizationId,
                facilityId =
                    clinicalNote.facilityId,
                encounterId =
                    clinicalNote.encounterId,
                chiefComplaint =
                    clinicalNote.chiefComplaint,
                subjective =
                    clinicalNote.subjective,
                objective =
                    clinicalNote.objective,
                assessment =
                    clinicalNote.assessment,
                plan =
                    clinicalNote.plan,
                createdByUserId =
                    clinicalNote.createdByUserId,
                updatedByUserId =
                    clinicalNote.updatedByUserId,
                createdAt =
                    clinicalNote.createdAt,
                updatedAt =
                    clinicalNote.updatedAt
            )
    }
}