package com.clinic.platform.modules.scheduling.application

import com.clinic.platform.modules.iam.domain.FacilityAssignmentRepository
import com.clinic.platform.modules.iam.domain.FacilityAssignmentStatus
import com.clinic.platform.modules.organization.domain.FacilityRepository
import com.clinic.platform.modules.organization.domain.FacilityStatus
import com.clinic.platform.modules.organization.domain.OrganizationRepository
import com.clinic.platform.modules.organization.domain.OrganizationStatus
import com.clinic.platform.modules.practitioner.domain.PractitionerRepository
import com.clinic.platform.modules.practitioner.domain.PractitionerStatus
import com.clinic.platform.modules.scheduling.domain.AvailabilityExceptionStatus
import com.clinic.platform.modules.scheduling.domain.AvailabilityExceptionType
import com.clinic.platform.modules.scheduling.domain.FacilitySchedulingSettingsRepository
import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityException
import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityExceptionRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import com.clinic.platform.shared.errors.ResourceStateConflictException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class CreatePractitionerAvailabilityExceptionUseCase(
    private val organizationRepository: OrganizationRepository,
    private val facilityRepository: FacilityRepository,
    private val practitionerRepository: PractitionerRepository,
    private val facilityAssignmentRepository: FacilityAssignmentRepository,
    private val facilitySchedulingSettingsRepository:
        FacilitySchedulingSettingsRepository,
    private val practitionerAvailabilityExceptionRepository:
        PractitionerAvailabilityExceptionRepository
) {

    @Transactional
    fun execute(
        organizationId: UUID,
        facilityId: UUID,
        practitionerId: UUID,
        exceptionType: AvailabilityExceptionType,
        startAt: Instant,
        endAt: Instant,
        reason: String?,
        actorUserId: UUID
    ): PractitionerAvailabilityException {

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

        val practitioner =
            practitionerRepository.findById(practitionerId)
                ?: throw ResourceNotFoundException(
                    "Practitioner not found in organization: $practitionerId"
                )

        if (practitioner.organizationId != organizationId) {
            throw ResourceNotFoundException(
                "Practitioner not found in organization: $practitionerId"
            )
        }

        if (practitioner.status != PractitionerStatus.ACTIVE) {
            throw ResourceStateConflictException(
                message =
                    "Practitioner is not active for this operation",
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

        if (
            !facilitySchedulingSettingsRepository
                .existsByOrganizationIdAndFacilityId(
                    organizationId,
                    facilityId
                )
        ) {
            throw ResourceStateConflictException(
                message =
                    "Scheduling settings are not configured for facility",
                code =
                    "SCHEDULING_SETTINGS_NOT_CONFIGURED",
                currentState =
                    "NOT_CONFIGURED"
            )
        }

        val candidate =
            PractitionerAvailabilityException.create(
                organizationId = organizationId,
                facilityId = facilityId,
                practitionerId = practitionerId,
                exceptionType = exceptionType,
                startAt = startAt,
                endAt = endAt,
                reason = reason,
                actorUserId = actorUserId
            )

        val hasOverlap =
            practitionerAvailabilityExceptionRepository
                .findAllByOrganizationIdAndFacilityIdAndPractitionerId(
                    organizationId,
                    facilityId,
                    practitionerId
                )
                .any { existing ->
                    existing.status ==
                        AvailabilityExceptionStatus.ACTIVE &&
                        timeRangesOverlap(
                            firstStart = existing.startAt,
                            firstEnd = existing.endAt,
                            secondStart = candidate.startAt,
                            secondEnd = candidate.endAt
                        )
                }

        if (hasOverlap) {
            throw ResourceStateConflictException(
                message =
                    "Practitioner availability exception overlaps an existing active exception",
                code =
                    "SCHEDULE_EXCEPTION_OVERLAP",
                currentState =
                    "OVERLAPPING"
            )
        }

        return practitionerAvailabilityExceptionRepository.save(
            candidate
        )
    }

    private fun timeRangesOverlap(
        firstStart: Instant,
        firstEnd: Instant,
        secondStart: Instant,
        secondEnd: Instant
    ): Boolean =
        firstStart.isBefore(secondEnd) &&
            secondStart.isBefore(firstEnd)
}