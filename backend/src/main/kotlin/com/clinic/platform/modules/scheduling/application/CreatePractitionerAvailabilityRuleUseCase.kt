package com.clinic.platform.modules.scheduling.application

import com.clinic.platform.modules.iam.domain.FacilityAssignmentRepository
import com.clinic.platform.modules.iam.domain.FacilityAssignmentStatus
import com.clinic.platform.modules.organization.domain.FacilityRepository
import com.clinic.platform.modules.organization.domain.FacilityStatus
import com.clinic.platform.modules.organization.domain.OrganizationRepository
import com.clinic.platform.modules.organization.domain.OrganizationStatus
import com.clinic.platform.modules.practitioner.domain.PractitionerRepository
import com.clinic.platform.modules.practitioner.domain.PractitionerStatus
import com.clinic.platform.modules.scheduling.domain.AvailabilityRuleStatus
import com.clinic.platform.modules.scheduling.domain.FacilitySchedulingSettingsRepository
import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityRule
import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityRuleRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import com.clinic.platform.shared.errors.ResourceStateConflictException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@Service
class CreatePractitionerAvailabilityRuleUseCase(
    private val organizationRepository: OrganizationRepository,
    private val facilityRepository: FacilityRepository,
    private val practitionerRepository: PractitionerRepository,
    private val facilityAssignmentRepository: FacilityAssignmentRepository,
    private val facilitySchedulingSettingsRepository:
        FacilitySchedulingSettingsRepository,
    private val practitionerAvailabilityRuleRepository:
        PractitionerAvailabilityRuleRepository
) {

    @Transactional
    fun execute(
        organizationId: UUID,
        facilityId: UUID,
        practitionerId: UUID,
        dayOfWeek: Int,
        startLocalTime: LocalTime,
        endLocalTime: LocalTime,
        effectiveFrom: LocalDate,
        effectiveTo: LocalDate?,
        actorUserId: UUID
    ): PractitionerAvailabilityRule {

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

        /*
         * Build the candidate first so domain validation remains the
         * canonical validation for day-of-week, time range and
         * effective date range.
         */
        val candidate =
            PractitionerAvailabilityRule.create(
                organizationId = organizationId,
                facilityId = facilityId,
                practitionerId = practitionerId,
                dayOfWeek = dayOfWeek,
                startLocalTime = startLocalTime,
                endLocalTime = endLocalTime,
                effectiveFrom = effectiveFrom,
                effectiveTo = effectiveTo,
                actorUserId = actorUserId
            )

        val hasOverlap =
            practitionerAvailabilityRuleRepository
                .findAllByOrganizationIdAndFacilityIdAndPractitionerId(
                    organizationId,
                    facilityId,
                    practitionerId
                )
                .any { existing ->
                    existing.status == AvailabilityRuleStatus.ACTIVE &&
                        existing.dayOfWeek == candidate.dayOfWeek &&
                        dateRangesOverlap(
                            firstFrom = existing.effectiveFrom,
                            firstTo = existing.effectiveTo,
                            secondFrom = candidate.effectiveFrom,
                            secondTo = candidate.effectiveTo
                        ) &&
                        timeRangesOverlap(
                            firstStart = existing.startLocalTime,
                            firstEnd = existing.endLocalTime,
                            secondStart = candidate.startLocalTime,
                            secondEnd = candidate.endLocalTime
                        )
                }

        if (hasOverlap) {
            throw ResourceStateConflictException(
                message =
                    "Practitioner availability rule overlaps an existing active rule",
                code =
                    "SCHEDULE_RULE_OVERLAP",
                currentState =
                    "OVERLAPPING"
            )
        }

        return practitionerAvailabilityRuleRepository.save(
            candidate
        )
    }

    private fun dateRangesOverlap(
        firstFrom: LocalDate,
        firstTo: LocalDate?,
        secondFrom: LocalDate,
        secondTo: LocalDate?
    ): Boolean {

        val firstEnd =
            firstTo ?: LocalDate.MAX

        val secondEnd =
            secondTo ?: LocalDate.MAX

        return !firstFrom.isAfter(secondEnd) &&
            !secondFrom.isAfter(firstEnd)
    }

    private fun timeRangesOverlap(
        firstStart: LocalTime,
        firstEnd: LocalTime,
        secondStart: LocalTime,
        secondEnd: LocalTime
    ): Boolean =
        firstStart.isBefore(secondEnd) &&
            secondStart.isBefore(firstEnd)
}