package com.clinic.platform.modules.encounter.application

import com.clinic.platform.modules.appointment.domain.AppointmentRepository
import com.clinic.platform.modules.appointment.domain.AppointmentStatus
import com.clinic.platform.modules.encounter.domain.ClinicalNote
import com.clinic.platform.modules.encounter.domain.ClinicalNoteRepository
import com.clinic.platform.modules.encounter.domain.Encounter
import com.clinic.platform.modules.encounter.domain.EncounterRepository
import com.clinic.platform.modules.reception.domain.QueueEntryRepository
import com.clinic.platform.modules.reception.domain.QueueEntryStatus
import com.clinic.platform.shared.errors.ResourceAlreadyExistsException
import com.clinic.platform.shared.errors.ResourceNotFoundException
import com.clinic.platform.shared.errors.ResourceStateConflictException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class StartEncounterUseCase(
    private val queueEntryRepository:
        QueueEntryRepository,
    private val appointmentRepository:
        AppointmentRepository,
    private val encounterRepository:
        EncounterRepository,
    private val clinicalNoteRepository:
        ClinicalNoteRepository
) {

    @Transactional
    fun execute(
        organizationId: UUID,
        facilityId: UUID,
        queueEntryId: UUID,
        actorUserId: UUID
    ): Encounter {

        val queueEntry =
            queueEntryRepository.findById(
                queueEntryId
            )
                ?: throw ResourceNotFoundException(
                    "Queue entry not found: $queueEntryId"
                )

        if (
            queueEntry.organizationId != organizationId ||
            queueEntry.facilityId != facilityId
        ) {
            throw ResourceNotFoundException(
                "Queue entry not found: $queueEntryId"
            )
        }

        if (
            queueEntry.status !=
            QueueEntryStatus.SERVING
        ) {
            throw ResourceStateConflictException(
                message =
                    "Queue entry must be serving before an encounter can start",
                code =
                    "QUEUE_ENTRY_NOT_SERVING",
                currentState =
                    queueEntry.status.name
            )
        }

        val appointment =
            appointmentRepository.findById(
                queueEntry.appointmentId
            )
                ?: throw ResourceNotFoundException(
                    "Appointment not found: ${queueEntry.appointmentId}"
                )

        if (
            appointment.organizationId != organizationId ||
            appointment.facilityId != facilityId
        ) {
            throw ResourceNotFoundException(
                "Appointment not found: ${queueEntry.appointmentId}"
            )
        }

        if (
            appointment.status !=
            AppointmentStatus.SCHEDULED
        ) {
            throw ResourceStateConflictException(
                message =
                    "Appointment must be scheduled before an encounter can start",
                code =
                    "APPOINTMENT_NOT_SCHEDULED_FOR_ENCOUNTER",
                currentState =
                    appointment.status.name
            )
        }

        if (
            queueEntry.appointmentId != appointment.id ||
            queueEntry.patientId != appointment.patientId ||
            queueEntry.practitionerId != appointment.practitionerId
        ) {
            throw ResourceStateConflictException(
                message =
                    "Queue entry and appointment clinical context do not match",
                code =
                    "QUEUE_APPOINTMENT_CONTEXT_MISMATCH",
                currentState =
                    "INCONSISTENT_CONTEXT"
            )
        }

        if (
            encounterRepository
                .existsByOrganizationIdAndAppointmentId(
                    organizationId =
                        organizationId,
                    appointmentId =
                        appointment.id
                )
        ) {
            throw ResourceAlreadyExistsException(
                "Encounter already exists for appointment: ${appointment.id}"
            )
        }

        if (
            encounterRepository
                .existsByOrganizationIdAndQueueEntryId(
                    organizationId =
                        organizationId,
                    queueEntryId =
                        queueEntry.id
                )
        ) {
            throw ResourceAlreadyExistsException(
                "Encounter already exists for queue entry: ${queueEntry.id}"
            )
        }

        val encounter =
            Encounter.create(
                organizationId =
                    organizationId,
                facilityId =
                    facilityId,
                appointmentId =
                    appointment.id,
                queueEntryId =
                    queueEntry.id,
                patientId =
                    appointment.patientId,
                practitionerId =
                    appointment.practitionerId,
                actorUserId =
                    actorUserId
            )

        val savedEncounter =
            encounterRepository.save(
                encounter
            )

        val clinicalNote =
            ClinicalNote.create(
                organizationId =
                    organizationId,
                facilityId =
                    facilityId,
                encounterId =
                    savedEncounter.id,
                actorUserId =
                    actorUserId
            )

        clinicalNoteRepository.save(
            clinicalNote
        )

        return savedEncounter
    }
}