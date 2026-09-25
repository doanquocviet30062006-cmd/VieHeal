package com.clinic.platform.modules.scheduling.domain

import java.util.UUID

interface PractitionerAvailabilityExceptionRepository {

    fun save(
        exception: PractitionerAvailabilityException
    ): PractitionerAvailabilityException

    fun findById(
        id: UUID
    ): PractitionerAvailabilityException?

    fun findAllByOrganizationIdAndFacilityIdAndPractitionerId(
        organizationId: UUID,
        facilityId: UUID,
        practitionerId: UUID
    ): List<PractitionerAvailabilityException>
}