package com.clinic.platform.modules.encounter.domain

import com.clinic.platform.shared.errors.DomainValidationException
import java.time.Instant
import java.util.UUID

data class ClinicalNote(
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

        private const val CHIEF_COMPLAINT_MAX_LENGTH =
            2000

        private const val SECTION_MAX_LENGTH =
            20000

        fun create(
            organizationId: UUID,
            facilityId: UUID,
            encounterId: UUID,
            actorUserId: UUID
        ): ClinicalNote {

            val now =
                Instant.now()

            return ClinicalNote(
                id = UUID.randomUUID(),
                organizationId = organizationId,
                facilityId = facilityId,
                encounterId = encounterId,
                chiefComplaint = null,
                subjective = null,
                objective = null,
                assessment = null,
                plan = null,
                createdByUserId = actorUserId,
                updatedByUserId = actorUserId,
                createdAt = now,
                updatedAt = now
            )
        }

        private fun normalizeOptional(
            value: String?
        ): String? =
            value
                ?.trim()
                ?.takeIf {
                    it.isNotBlank()
                }

        private fun validateContent(
            chiefComplaint: String?,
            subjective: String?,
            objective: String?,
            assessment: String?,
            plan: String?
        ) {

            if (
                chiefComplaint != null &&
                chiefComplaint.length >
                CHIEF_COMPLAINT_MAX_LENGTH
            ) {
                throw DomainValidationException(
                    message =
                        "Clinical note chief complaint must not exceed 2000 characters",
                    code =
                        "CLINICAL_NOTE_CHIEF_COMPLAINT_TOO_LONG"
                )
            }

            validateSection(
                sectionName =
                    "subjective",
                value =
                    subjective,
                errorCode =
                    "CLINICAL_NOTE_SUBJECTIVE_TOO_LONG"
            )

            validateSection(
                sectionName =
                    "objective",
                value =
                    objective,
                errorCode =
                    "CLINICAL_NOTE_OBJECTIVE_TOO_LONG"
            )

            validateSection(
                sectionName =
                    "assessment",
                value =
                    assessment,
                errorCode =
                    "CLINICAL_NOTE_ASSESSMENT_TOO_LONG"
            )

            validateSection(
                sectionName =
                    "plan",
                value =
                    plan,
                errorCode =
                    "CLINICAL_NOTE_PLAN_TOO_LONG"
            )
        }

        private fun validateSection(
            sectionName: String,
            value: String?,
            errorCode: String
        ) {

            if (
                value != null &&
                value.length >
                SECTION_MAX_LENGTH
            ) {
                throw DomainValidationException(
                    message =
                        "Clinical note $sectionName must not exceed 20000 characters",
                    code =
                        errorCode
                )
            }
        }
    }

    fun update(
        chiefComplaint: String?,
        subjective: String?,
        objective: String?,
        assessment: String?,
        plan: String?,
        actorUserId: UUID
    ): ClinicalNote {

        val normalizedChiefComplaint =
            normalizeOptional(
                chiefComplaint
            )

        val normalizedSubjective =
            normalizeOptional(
                subjective
            )

        val normalizedObjective =
            normalizeOptional(
                objective
            )

        val normalizedAssessment =
            normalizeOptional(
                assessment
            )

        val normalizedPlan =
            normalizeOptional(
                plan
            )

        validateContent(
            chiefComplaint =
                normalizedChiefComplaint,
            subjective =
                normalizedSubjective,
            objective =
                normalizedObjective,
            assessment =
                normalizedAssessment,
            plan =
                normalizedPlan
        )

        return copy(
            chiefComplaint =
                normalizedChiefComplaint,
            subjective =
                normalizedSubjective,
            objective =
                normalizedObjective,
            assessment =
                normalizedAssessment,
            plan =
                normalizedPlan,
            updatedByUserId =
                actorUserId,
            updatedAt =
                Instant.now()
        )
    }
}