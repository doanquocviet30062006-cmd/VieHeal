package com.clinic.platform.modules.reception.domain

import com.clinic.platform.shared.errors.DomainValidationException
import java.time.Instant
import java.util.UUID

data class QueueEntry(
    val id: UUID,
    val organizationId: UUID,
    val facilityId: UUID,
    val appointmentId: UUID,
    val patientId: UUID,
    val practitionerId: UUID,
    val status: QueueEntryStatus,
    val checkedInAt: Instant,
    val calledAt: Instant?,
    val servingStartedAt: Instant?,
    val completedAt: Instant?,
    val cancelledAt: Instant?,
    val note: String?,
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
            patientId: UUID,
            practitionerId: UUID,
            note: String?,
            actorUserId: UUID
        ): QueueEntry {

            val normalizedNote =
                normalizeNote(note)

            validateNote(
                normalizedNote
            )

            val now =
                Instant.now()

            return QueueEntry(
                id = UUID.randomUUID(),
                organizationId = organizationId,
                facilityId = facilityId,
                appointmentId = appointmentId,
                patientId = patientId,
                practitionerId = practitionerId,
                status = QueueEntryStatus.WAITING,
                checkedInAt = now,
                calledAt = null,
                servingStartedAt = null,
                completedAt = null,
                cancelledAt = null,
                note = normalizedNote,
                createdByUserId = actorUserId,
                updatedByUserId = actorUserId,
                createdAt = now,
                updatedAt = now
            )
        }

        private fun normalizeNote(
            value: String?
        ): String? =
            value
                ?.trim()
                ?.takeIf { it.isNotBlank() }

        private fun validateNote(
            note: String?
        ) {
            if (
                note != null &&
                note.length > 1000
            ) {
                throw DomainValidationException(
                    message =
                        "Queue entry note must not exceed 1000 characters",
                    code =
                        "QUEUE_NOTE_TOO_LONG"
                )
            }
        }

        private fun invalidTransition(
            from: QueueEntryStatus,
            to: QueueEntryStatus
        ): Nothing {
            throw DomainValidationException(
                message =
                    "Queue entry cannot transition from ${from.name} to ${to.name}",
                code =
                    "QUEUE_INVALID_STATUS_TRANSITION"
            )
        }
    }

    fun call(
        actorUserId: UUID
    ): QueueEntry {

        if (status != QueueEntryStatus.WAITING) {
            invalidTransition(
                from = status,
                to = QueueEntryStatus.CALLED
            )
        }

        val now =
            Instant.now()

        return copy(
            status = QueueEntryStatus.CALLED,
            calledAt = now,
            updatedByUserId = actorUserId,
            updatedAt = now
        )
    }

    fun startServing(
        actorUserId: UUID
    ): QueueEntry {

        if (status != QueueEntryStatus.CALLED) {
            invalidTransition(
                from = status,
                to = QueueEntryStatus.SERVING
            )
        }

        val now =
            Instant.now()

        return copy(
            status = QueueEntryStatus.SERVING,
            servingStartedAt = now,
            updatedByUserId = actorUserId,
            updatedAt = now
        )
    }

    fun complete(
        actorUserId: UUID
    ): QueueEntry {

        if (status != QueueEntryStatus.SERVING) {
            invalidTransition(
                from = status,
                to = QueueEntryStatus.COMPLETED
            )
        }

        val now =
            Instant.now()

        return copy(
            status = QueueEntryStatus.COMPLETED,
            completedAt = now,
            updatedByUserId = actorUserId,
            updatedAt = now
        )
    }

    fun cancel(
        actorUserId: UUID
    ): QueueEntry {

        if (
            status != QueueEntryStatus.WAITING &&
            status != QueueEntryStatus.CALLED &&
            status != QueueEntryStatus.SERVING
        ) {
            invalidTransition(
                from = status,
                to = QueueEntryStatus.CANCELLED
            )
        }

        val now =
            Instant.now()

        return copy(
            status = QueueEntryStatus.CANCELLED,
            cancelledAt = now,
            updatedByUserId = actorUserId,
            updatedAt = now
        )
    }

    fun updateNote(
        note: String?,
        actorUserId: UUID
    ): QueueEntry {

        if (
            status == QueueEntryStatus.COMPLETED ||
            status == QueueEntryStatus.CANCELLED
        ) {
            throw DomainValidationException(
                message =
                    "Terminal queue entry cannot be updated",
                code =
                    "QUEUE_ENTRY_TERMINAL"
            )
        }

        val normalizedNote =
            normalizeNote(note)

        validateNote(
            normalizedNote
        )

        return copy(
            note = normalizedNote,
            updatedByUserId = actorUserId,
            updatedAt = Instant.now()
        )
    }
}