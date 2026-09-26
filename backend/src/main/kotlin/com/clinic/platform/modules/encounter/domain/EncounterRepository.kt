package com.clinic.platform.modules.encounter.domain

import java.util.UUID

interface EncounterRepository {

    fun save(
        encounter: Encounter
    ): Encounter

    fun findById(
        id: UUID
    ): Encounter?

    fun findByOrganizationIdAndAppointmentId(
        organizationId: UUID,
        appointmentId: UUID
    ): Encounter?

    fun findByOrganizationIdAndQueueEntryId(
        organizationId: UUID,
        queueEntryId: UUID
    ): Encounter?

    fun existsByOrganizationIdAndAppointmentId(
        organizationId: UUID,
        appointmentId: UUID
    ): Boolean

    fun existsByOrganizationIdAndQueueEntryId(
        organizationId: UUID,
        queueEntryId: UUID
    ): Boolean

    fun findAllByOrganizationIdAndFacilityId(
        organizationId: UUID,
        facilityId: UUID
    ): List<Encounter>

    fun findAllByOrganizationIdAndFacilityIdAndStatus(
        organizationId: UUID,
        facilityId: UUID,
        status: EncounterStatus
    ): List<Encounter>
}