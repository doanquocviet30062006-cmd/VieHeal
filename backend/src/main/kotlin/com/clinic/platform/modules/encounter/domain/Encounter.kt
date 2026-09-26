package com.clinic.platform.modules.encounter.domain

import com.clinic.platform.shared.errors.DomainValidationException
import java.time.Instant
import java.util.UUID

data class Encounter(
    val id: UUID,
    val organizationId: UUID,
    val facilityId: UUID,
    val appointmentId: UUID,
    val queueEntryId: UUID,
    val patientId: UUID,
    val practitionerId: UUID,
    val status: EncounterStatus,
    val startedAt: Instant,
    val completedAt: Instant?,
    val cancelledAt: Instant?,
    val cancellationReason: String?,
    val createdByUserId: UUID,
    val updatedByUserId: UUID,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun create(
            organizationId: UUID,
            facilityId: UUID,
            appointmentId: UUID,
            queueEntryId: UUID,
            patientId: UUID,
            practitionerId: UUID,
            actorUserId: UUID
        ): Encounter {

            val now =
                Instant.now()

            return Encounter(
                id = UUID.randomUUID(),
                organizationId = organizationId,
                facilityId = facilityId,
                appointmentId = appointmentId,
                queueEntryId = queueEntryId,
                patientId = patientId,
                practitionerId = practitionerId,
                status = EncounterStatus.IN_PROGRESS,
                startedAt = now,
                completedAt = null,
                cancelledAt = null,
                cancellationReason = null,
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

        private fun validateCancellationReason(
            cancellationReason: String?
        ): String {

            val normalized =
                normalizeOptional(
                    cancellationReason
                )
                    ?: throw DomainValidationException(
                        message =
                            "Encounter cancellation reason is required",
                        code =
                            "ENCOUNTER_CANCELLATION_REASON_REQUIRED"
                    )

            if (
                normalized.length > 500
            ) {
                throw DomainValidationException(
                    message =
                        "Encounter cancellation reason must not exceed 500 characters",
                    code =
                        "ENCOUNTER_CANCELLATION_REASON_TOO_LONG"
                )
            }

            return normalized
        }
    }

    fun complete(
        actorUserId: UUID
    ): Encounter {

        requireInProgress(
            targetStatus =
                EncounterStatus.COMPLETED
        )

        val now =
            Instant.now()

        return copy(
            status =
                EncounterStatus.COMPLETED,
            completedAt = now,
            cancelledAt = null,
            cancellationReason = null,
            updatedByUserId =
                actorUserId,
            updatedAt = now
        )
    }

    fun cancel(
        cancellationReason: String?,
        actorUserId: UUID
    ): Encounter {

        requireInProgress(
            targetStatus =
                EncounterStatus.CANCELLED
        )

        val normalizedReason =
            validateCancellationReason(
                cancellationReason
            )

        val now =
            Instant.now()

        return copy(
            status =
                EncounterStatus.CANCELLED,
            completedAt = null,
            cancelledAt = now,
            cancellationReason =
                normalizedReason,
            updatedByUserId =
                actorUserId,
            updatedAt = now
        )
    }

    private fun requireInProgress(
        targetStatus: EncounterStatus
    ) {

        if (
            status != EncounterStatus.IN_PROGRESS
        ) {
            throw DomainValidationException(
                message =
                    "Encounter cannot transition from ${status.name} to ${targetStatus.name}",
                code =
                    "ENCOUNTER_INVALID_STATUS_TRANSITION"
            )
        }
    }
}