package com.clinic.platform.modules.encounter.api.dto

import com.clinic.platform.modules.encounter.domain.Encounter
import java.time.Instant
import java.util.UUID

data class EncounterResponse(
    val id: UUID,
    val organizationId: UUID,
    val facilityId: UUID,
    val appointmentId: UUID,
    val queueEntryId: UUID,
    val patientId: UUID,
    val practitionerId: UUID,
    val status: String,
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

        fun from(
            encounter: Encounter
        ): EncounterResponse =
            EncounterResponse(
                id =
                    encounter.id,
                organizationId =
                    encounter.organizationId,
                facilityId =
                    encounter.facilityId,
                appointmentId =
                    encounter.appointmentId,
                queueEntryId =
                    encounter.queueEntryId,
                patientId =
                    encounter.patientId,
                practitionerId =
                    encounter.practitionerId,
                status =
                    encounter.status.name,
                startedAt =
                    encounter.startedAt,
                completedAt =
                    encounter.completedAt,
                cancelledAt =
                    encounter.cancelledAt,
                cancellationReason =
                    encounter.cancellationReason,
                createdByUserId =
                    encounter.createdByUserId,
                updatedByUserId =
                    encounter.updatedByUserId,
                createdAt =
                    encounter.createdAt,
                updatedAt =
                    encounter.updatedAt
            )
    }
}