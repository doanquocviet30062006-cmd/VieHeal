package com.clinic.platform.modules.appointment.application

import com.clinic.platform.modules.appointment.domain.Appointment
import com.clinic.platform.modules.appointment.domain.AppointmentRepository
import com.clinic.platform.modules.iam.domain.FacilityAssignmentRepository
import com.clinic.platform.modules.iam.domain.FacilityAssignmentStatus
import com.clinic.platform.modules.organization.domain.FacilityRepository
import com.clinic.platform.modules.organization.domain.FacilityStatus
import com.clinic.platform.modules.organization.domain.OrganizationRepository
import com.clinic.platform.modules.organization.domain.OrganizationStatus
import com.clinic.platform.modules.patient.domain.PatientRepository
import com.clinic.platform.modules.patient.domain.PatientStatus
import com.clinic.platform.modules.practitioner.domain.PractitionerRepository
import com.clinic.platform.modules.practitioner.domain.PractitionerStatus
import com.clinic.platform.modules.scheduling.domain.AvailabilityExceptionStatus
import com.clinic.platform.modules.scheduling.domain.AvailabilityExceptionType
import com.clinic.platform.modules.scheduling.domain.AvailabilityRuleStatus
import com.clinic.platform.modules.scheduling.domain.FacilitySchedulingSettingsRepository
import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityException
import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityExceptionRepository
import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityRule
import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityRuleRepository
import com.clinic.platform.modules.servicecatalog.domain.FacilityServiceRepository
import com.clinic.platform.modules.servicecatalog.domain.FacilityServiceStatus
import com.clinic.platform.shared.errors.ResourceNotFoundException
import com.clinic.platform.shared.errors.ResourceStateConflictException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID

@Service
class RescheduleAppointmentUseCase(
    private val organizationRepository: OrganizationRepository,
    private val facilityRepository: FacilityRepository,
    private val patientRepository: PatientRepository,
    private val practitionerRepository: PractitionerRepository,
    private val facilityAssignmentRepository: FacilityAssignmentRepository,
    private val facilityServiceRepository: FacilityServiceRepository,
    private val facilitySchedulingSettingsRepository:
        FacilitySchedulingSettingsRepository,
    private val practitionerAvailabilityRuleRepository:
        PractitionerAvailabilityRuleRepository,
    private val practitionerAvailabilityExceptionRepository:
        PractitionerAvailabilityExceptionRepository,
    private val appointmentRepository: AppointmentRepository
) {

    @Transactional
    fun execute(
        organizationId: UUID,
        facilityId: UUID,
        appointmentId: UUID,
        scheduledStartAt: Instant,
        actorUserId: UUID
    ): Appointment {

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

        val patient =
            patientRepository.findById(appointment.patientId)
                ?: throw ResourceNotFoundException(
                    "Patient not found in organization: ${appointment.patientId}"
                )

        if (patient.organizationId != organizationId) {
            throw ResourceNotFoundException(
                "Patient not found in organization: ${appointment.patientId}"
            )
        }

        if (patient.status != PatientStatus.ACTIVE) {
            throw ResourceStateConflictException(
                message =
                    "Patient is not active for appointment rescheduling",
                code =
                    "PATIENT_NOT_ACTIVE",
                currentState =
                    patient.status.name
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

        if (practitioner.status != PractitionerStatus.ACTIVE) {
            throw ResourceStateConflictException(
                message =
                    "Practitioner is not active for appointment rescheduling",
                code =
                    "PRACTITIONER_NOT_ACTIVE",
                currentState =
                    practitioner.status.name
            )
        }

        val hasActiveFacilityAssignment =
            facilityAssignmentRepository
                .findAllByMembershipId(
                    practitioner.membershipId
                )
                .any { assignment ->
                    assignment.facilityId == facilityId &&
                        assignment.status ==
                            FacilityAssignmentStatus.ACTIVE
                }

        if (!hasActiveFacilityAssignment) {
            throw ResourceStateConflictException(
                message =
                    "Practitioner is not actively assigned to facility",
                code =
                    "PRACTITIONER_NOT_ASSIGNED_TO_FACILITY",
                currentState =
                    "NOT_ASSIGNED"
            )
        }

        val facilityService =
            facilityServiceRepository.findById(
                appointment.facilityServiceId
            )
                ?: throw ResourceNotFoundException(
                    "Facility service not found in facility: ${appointment.facilityServiceId}"
                )

        if (
            facilityService.organizationId != organizationId ||
            facilityService.facilityId != facilityId
        ) {
            throw ResourceNotFoundException(
                "Facility service not found in facility: ${appointment.facilityServiceId}"
            )
        }

        if (facilityService.status != FacilityServiceStatus.ACTIVE) {
            throw ResourceStateConflictException(
                message =
                    "Facility service is not active for appointment rescheduling",
                code =
                    "FACILITY_SERVICE_NOT_ACTIVE",
                currentState =
                    facilityService.status.name
            )
        }

        if (!facilityService.bookingEnabled) {
            throw ResourceStateConflictException(
                message =
                    "Facility service is not enabled for appointment booking",
                code =
                    "FACILITY_SERVICE_BOOKING_DISABLED",
                currentState =
                    "BOOKING_DISABLED"
            )
        }

        val schedulingSettings =
            facilitySchedulingSettingsRepository
                .findByOrganizationIdAndFacilityId(
                    organizationId,
                    facilityId
                )
                ?: throw ResourceStateConflictException(
                    message =
                        "Scheduling settings are not configured for facility",
                    code =
                        "SCHEDULING_SETTINGS_NOT_CONFIGURED",
                    currentState =
                        "NOT_CONFIGURED"
                )

        val scheduledEndAt =
            scheduledStartAt.plusSeconds(
                facilityService.durationMinutes.toLong() * 60L
            )

        val availabilityRules =
            practitionerAvailabilityRuleRepository
                .findAllByOrganizationIdAndFacilityIdAndPractitionerId(
                    organizationId,
                    facilityId,
                    appointment.practitionerId
                )

        val availabilityExceptions =
            practitionerAvailabilityExceptionRepository
                .findAllByOrganizationIdAndFacilityIdAndPractitionerId(
                    organizationId,
                    facilityId,
                    appointment.practitionerId
                )

        validatePractitionerAvailability(
            scheduledStartAt = scheduledStartAt,
            scheduledEndAt = scheduledEndAt,
            timeZoneId = schedulingSettings.timeZoneId,
            availabilityRules = availabilityRules,
            availabilityExceptions = availabilityExceptions
        )

        if (
            appointmentRepository
                .existsScheduledPractitionerOverlap(
                    organizationId = organizationId,
                    practitionerId = appointment.practitionerId,
                    scheduledStartAt = scheduledStartAt,
                    scheduledEndAt = scheduledEndAt,
                    excludeAppointmentId = appointment.id
                )
        ) {
            throw ResourceStateConflictException(
                message =
                    "Practitioner already has an appointment during this time",
                code =
                    "APPOINTMENT_PRACTITIONER_TIME_CONFLICT",
                currentState =
                    "TIME_CONFLICT"
            )
        }

        if (
            appointmentRepository
                .existsScheduledPatientOverlap(
                    organizationId = organizationId,
                    patientId = appointment.patientId,
                    scheduledStartAt = scheduledStartAt,
                    scheduledEndAt = scheduledEndAt,
                    excludeAppointmentId = appointment.id
                )
        ) {
            throw ResourceStateConflictException(
                message =
                    "Patient already has an appointment during this time",
                code =
                    "APPOINTMENT_PATIENT_TIME_CONFLICT",
                currentState =
                    "TIME_CONFLICT"
            )
        }

        val updated =
            appointment.reschedule(
                scheduledStartAt = scheduledStartAt,
                scheduledEndAt = scheduledEndAt,
                actorUserId = actorUserId
            )

        return appointmentRepository.save(
            updated
        )
    }

    private fun validatePractitionerAvailability(
        scheduledStartAt: Instant,
        scheduledEndAt: Instant,
        timeZoneId: String,
        availabilityRules: List<PractitionerAvailabilityRule>,
        availabilityExceptions:
            List<PractitionerAvailabilityException>
    ) {

        val activeExceptions =
            availabilityExceptions.filter {
                it.status == AvailabilityExceptionStatus.ACTIVE
            }

        val blockedByUnavailableException =
            activeExceptions.any { exception ->
                exception.exceptionType ==
                    AvailabilityExceptionType.UNAVAILABLE &&
                    timeRangesOverlap(
                        firstStart = scheduledStartAt,
                        firstEnd = scheduledEndAt,
                        secondStart = exception.startAt,
                        secondEnd = exception.endAt
                    )
            }

        if (blockedByUnavailableException) {
            throw ResourceStateConflictException(
                message =
                    "Practitioner is unavailable during the requested appointment time",
                code =
                    "APPOINTMENT_PRACTITIONER_UNAVAILABLE",
                currentState =
                    "UNAVAILABLE"
            )
        }

        val coveredByAvailableException =
            activeExceptions.any { exception ->
                exception.exceptionType ==
                    AvailabilityExceptionType.AVAILABLE &&
                    instantRangeContains(
                        containerStart = exception.startAt,
                        containerEnd = exception.endAt,
                        candidateStart = scheduledStartAt,
                        candidateEnd = scheduledEndAt
                    )
            }

        if (coveredByAvailableException) {
            return
        }

        val zoneId =
            ZoneId.of(timeZoneId)

        val localStart =
            scheduledStartAt.atZone(zoneId)

        val localEnd =
            scheduledEndAt.atZone(zoneId)

        if (localStart.toLocalDate() != localEnd.toLocalDate()) {
            throw ResourceStateConflictException(
                message =
                    "Requested appointment is outside practitioner availability",
                code =
                    "APPOINTMENT_OUTSIDE_PRACTITIONER_AVAILABILITY",
                currentState =
                    "OUTSIDE_AVAILABILITY"
            )
        }

        val appointmentDate =
            localStart.toLocalDate()

        val appointmentStartTime =
            localStart.toLocalTime()

        val appointmentEndTime =
            localEnd.toLocalTime()

        val appointmentDayOfWeek =
            localStart.dayOfWeek.value

        val coveredByRecurringRule =
            availabilityRules.any { rule ->
                rule.status == AvailabilityRuleStatus.ACTIVE &&
                    rule.dayOfWeek == appointmentDayOfWeek &&
                    dateIsInsideEffectiveRange(
                        date = appointmentDate,
                        effectiveFrom = rule.effectiveFrom,
                        effectiveTo = rule.effectiveTo
                    ) &&
                    localTimeRangeContains(
                        containerStart = rule.startLocalTime,
                        containerEnd = rule.endLocalTime,
                        candidateStart = appointmentStartTime,
                        candidateEnd = appointmentEndTime
                    )
            }

        if (!coveredByRecurringRule) {
            throw ResourceStateConflictException(
                message =
                    "Requested appointment is outside practitioner availability",
                code =
                    "APPOINTMENT_OUTSIDE_PRACTITIONER_AVAILABILITY",
                currentState =
                    "OUTSIDE_AVAILABILITY"
            )
        }
    }

    private fun dateIsInsideEffectiveRange(
        date: LocalDate,
        effectiveFrom: LocalDate,
        effectiveTo: LocalDate?
    ): Boolean =
        !date.isBefore(effectiveFrom) &&
            (
                effectiveTo == null ||
                    !date.isAfter(effectiveTo)
            )

    private fun localTimeRangeContains(
        containerStart: LocalTime,
        containerEnd: LocalTime,
        candidateStart: LocalTime,
        candidateEnd: LocalTime
    ): Boolean =
        !candidateStart.isBefore(containerStart) &&
            !candidateEnd.isAfter(containerEnd)

    private fun instantRangeContains(
        containerStart: Instant,
        containerEnd: Instant,
        candidateStart: Instant,
        candidateEnd: Instant
    ): Boolean =
        !candidateStart.isBefore(containerStart) &&
            !candidateEnd.isAfter(containerEnd)

    private fun timeRangesOverlap(
        firstStart: Instant,
        firstEnd: Instant,
        secondStart: Instant,
        secondEnd: Instant
    ): Boolean =
        firstStart.isBefore(secondEnd) &&
            secondStart.isBefore(firstEnd)
}