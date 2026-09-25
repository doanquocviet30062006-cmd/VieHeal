package com.clinic.platform.modules.reception.domain

import java.util.UUID

interface QueueEntryRepository {

    fun save(
        queueEntry: QueueEntry
    ): QueueEntry

    fun findById(
        id: UUID
    ): QueueEntry?

    fun findByOrganizationIdAndAppointmentId(
        organizationId: UUID,
        appointmentId: UUID
    ): QueueEntry?

    fun existsByOrganizationIdAndAppointmentId(
        organizationId: UUID,
        appointmentId: UUID
    ): Boolean

    fun findAllByOrganizationIdAndFacilityIdOrdered(
        organizationId: UUID,
        facilityId: UUID
    ): List<QueueEntry>

    fun findAllByOrganizationIdAndFacilityIdAndStatusOrdered(
        organizationId: UUID,
        facilityId: UUID,
        status: QueueEntryStatus
    ): List<QueueEntry>
}