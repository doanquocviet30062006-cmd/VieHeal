package com.clinic.platform.modules.reception.api.dto

import com.clinic.platform.modules.reception.domain.QueueEntry
import java.time.Instant
import java.util.UUID

data class QueueEntryResponse(
    val id: UUID,
    val organizationId: UUID,
    val facilityId: UUID,
    val appointmentId: UUID,
    val patientId: UUID,
    val practitionerId: UUID,
    val status: String,
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

        fun from(
            queueEntry: QueueEntry
        ): QueueEntryResponse =
            QueueEntryResponse(
                id = queueEntry.id,
                organizationId =
                    queueEntry.organizationId,
                facilityId =
                    queueEntry.facilityId,
                appointmentId =
                    queueEntry.appointmentId,
                patientId =
                    queueEntry.patientId,
                practitionerId =
                    queueEntry.practitionerId,
                status =
                    queueEntry.status.name,
                checkedInAt =
                    queueEntry.checkedInAt,
                calledAt =
                    queueEntry.calledAt,
                servingStartedAt =
                    queueEntry.servingStartedAt,
                completedAt =
                    queueEntry.completedAt,
                cancelledAt =
                    queueEntry.cancelledAt,
                note =
                    queueEntry.note,
                createdByUserId =
                    queueEntry.createdByUserId,
                updatedByUserId =
                    queueEntry.updatedByUserId,
                createdAt =
                    queueEntry.createdAt,
                updatedAt =
                    queueEntry.updatedAt
            )
    }
}