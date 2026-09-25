package com.clinic.platform.modules.reception.application

import com.clinic.platform.modules.appointment.domain.AppointmentRepository
import com.clinic.platform.modules.appointment.domain.AppointmentStatus
import com.clinic.platform.modules.organization.domain.FacilityRepository
import com.clinic.platform.modules.organization.domain.FacilityStatus
import com.clinic.platform.modules.organization.domain.OrganizationRepository
import com.clinic.platform.modules.organization.domain.OrganizationStatus
import com.clinic.platform.modules.patient.domain.PatientRepository
import com.clinic.platform.modules.practitioner.domain.PractitionerRepository
import com.clinic.platform.modules.reception.domain.QueueEntry
import com.clinic.platform.modules.reception.domain.QueueEntryRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import com.clinic.platform.shared.errors.ResourceStateConflictException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CheckInAppointmentUseCase(
    private val organizationRepository: OrganizationRepository,
    private val facilityRepository: FacilityRepository,
    private val appointmentRepository: AppointmentRepository,
    private val patientRepository: PatientRepository,
    private val practitionerRepository: PractitionerRepository,
    private val queueEntryRepository: QueueEntryRepository
) {

    @Transactional
    fun execute(
        organizationId: UUID,
        facilityId: UUID,
        appointmentId: UUID,
        note: String?,
        actorUserId: UUID
    ): QueueEntry {

        val organization =
            organizationRepository.findById(organizationId)
                ?: throw ResourceNotFoundException(
                    "Organization not found: $organizationId"
                )

        if (organization.status != OrganizationStatus.ACTIVE) {
            throw ResourceStateConflictException(
                message =
                    "Organization is not active for this operation",
                code =
                    "ORGANIZATION_NOT_ACTIVE",
                currentState =
                    organization.status.name
            )
        }

        val facility =
            facilityRepository.findById(facilityId)
                ?: throw ResourceNotFoundException(
                    "Facility not found in organization: $facilityId"
                )

        if (facility.organizationId != organizationId) {
            throw ResourceNotFoundException(
                "Facility not found in organization: $facilityId"
            )
        }

        if (facility.status != FacilityStatus.ACTIVE) {
            throw ResourceStateConflictException(
                message =
                    "Facility is not active for this operation",
                code =
                    "FACILITY_NOT_ACTIVE",
                currentState =
                    facility.status.name
            )
        }

        val appointment =
            appointmentRepository.findById(appointmentId)
                ?: throw ResourceNotFoundException(
                    "Appointment not found in facility: $appointmentId"
                )

        if (
            appointment.organizationId != organizationId ||
            appointment.facilityId != facilityId
        ) {
            throw ResourceNotFoundException(
                "Appointment not found in facility: $appointmentId"
            )
        }

        if (appointment.status != AppointmentStatus.SCHEDULED) {
            throw ResourceStateConflictException(
                message =
                    "Appointment is not scheduled for check-in",
                code =
                    "APPOINTMENT_NOT_SCHEDULED",
                currentState =
                    appointment.status.name
            )
        }

        val patient =
            patientRepository.findById(
                appointment.patientId
            )
                ?: throw ResourceNotFoundException(
                    "Patient not found in organization: ${appointment.patientId}"
                )

        if (patient.organizationId != organizationId) {
            throw ResourceNotFoundException(
                "Patient not found in organization: ${appointment.patientId}"
            )
        }

        val practitioner =
            practitionerRepository.findById(
                appointment.practitionerId
            )
                ?: throw ResourceNotFoundException(
                    "Practitioner not found in organization: ${appointment.practitionerId}"
                )

        if (practitioner.organizationId != organizationId) {
            throw ResourceNotFoundException(
                "Practitioner not found in organization: ${appointment.practitionerId}"
            )
        }

        val existingQueueEntry =
            queueEntryRepository
                .findByOrganizationIdAndAppointmentId(
                    organizationId = organizationId,
                    appointmentId = appointmentId
                )

        if (existingQueueEntry != null) {
            throw ResourceStateConflictException(
                message =
                    "Appointment has already been checked in",
                code =
                    "QUEUE_ENTRY_ALREADY_EXISTS",
                currentState =
                    existingQueueEntry.status.name
            )
        }

        val queueEntry =
            QueueEntry.create(
                organizationId = organizationId,
                facilityId = facilityId,
                appointmentId = appointment.id,
                patientId = appointment.patientId,
                practitionerId =
                    appointment.practitionerId,
                note = note,
                actorUserId = actorUserId
            )

        return queueEntryRepository.save(
            queueEntry
        )
    }
}