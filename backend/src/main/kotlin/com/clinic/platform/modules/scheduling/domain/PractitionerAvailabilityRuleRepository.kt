package com.clinic.platform.modules.scheduling.domain

import java.util.UUID

interface PractitionerAvailabilityRuleRepository {

    fun save(
        rule: PractitionerAvailabilityRule
    ): PractitionerAvailabilityRule

    fun findById(
        id: UUID
    ): PractitionerAvailabilityRule?

    fun findAllByOrganizationIdAndFacilityIdAndPractitionerId(
        organizationId: UUID,
        facilityId: UUID,
        practitionerId: UUID
    ): List<PractitionerAvailabilityRule>
}