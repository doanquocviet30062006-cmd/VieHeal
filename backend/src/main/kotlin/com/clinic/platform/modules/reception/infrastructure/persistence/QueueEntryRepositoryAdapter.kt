package com.clinic.platform.modules.reception.infrastructure.persistence

import com.clinic.platform.modules.reception.domain.QueueEntry
import com.clinic.platform.modules.reception.domain.QueueEntryRepository
import com.clinic.platform.modules.reception.domain.QueueEntryStatus
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class QueueEntryRepositoryAdapter(
    private val queueEntryJpaRepository:
        QueueEntryJpaRepository
) : QueueEntryRepository {

    override fun save(
        queueEntry: QueueEntry
    ): QueueEntry =
        queueEntryJpaRepository
            .save(
                queueEntry.toJpaEntity()
            )
            .toDomain()

    override fun findById(
        id: UUID
    ): QueueEntry? =
        queueEntryJpaRepository
            .findById(id)
            .orElse(null)
            ?.toDomain()

    override fun findByOrganizationIdAndAppointmentId(
        organizationId: UUID,
        appointmentId: UUID
    ): QueueEntry? =
        queueEntryJpaRepository
            .findByOrganizationIdAndAppointmentId(
                organizationId = organizationId,
                appointmentId = appointmentId
            )
            ?.toDomain()

    override fun existsByOrganizationIdAndAppointmentId(
        organizationId: UUID,
        appointmentId: UUID
    ): Boolean =
        queueEntryJpaRepository
            .existsByOrganizationIdAndAppointmentId(
                organizationId = organizationId,
                appointmentId = appointmentId
            )

    override fun findAllByOrganizationIdAndFacilityIdOrdered(
        organizationId: UUID,
        facilityId: UUID
    ): List<QueueEntry> =
        queueEntryJpaRepository
            .findAllByOrganizationIdAndFacilityIdOrderByCheckedInAtAscIdAsc(
                organizationId = organizationId,
                facilityId = facilityId
            )
            .map {
                it.toDomain()
            }

    override fun findAllByOrganizationIdAndFacilityIdAndStatusOrdered(
        organizationId: UUID,
        facilityId: UUID,
        status: QueueEntryStatus
    ): List<QueueEntry> =
        queueEntryJpaRepository
            .findAllByOrganizationIdAndFacilityIdAndStatusOrderByCheckedInAtAscIdAsc(
                organizationId = organizationId,
                facilityId = facilityId,
                status = status
            )
            .map {
                it.toDomain()
            }

    private fun QueueEntry.toJpaEntity():
        QueueEntryJpaEntity =
        QueueEntryJpaEntity(
            id = id,
            organizationId = organizationId,
            facilityId = facilityId,
            appointmentId = appointmentId,
            patientId = patientId,
            practitionerId = practitionerId,
            status = status,
            checkedInAt = checkedInAt,
            calledAt = calledAt,
            servingStartedAt = servingStartedAt,
            completedAt = completedAt,
            cancelledAt = cancelledAt,
            note = note,
            createdByUserId = createdByUserId,
            updatedByUserId = updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

    private fun QueueEntryJpaEntity.toDomain():
        QueueEntry =
        QueueEntry(
            id = id,
            organizationId = organizationId,
            facilityId = facilityId,
            appointmentId = appointmentId,
            patientId = patientId,
            practitionerId = practitionerId,
            status = status,
            checkedInAt = checkedInAt,
            calledAt = calledAt,
            servingStartedAt = servingStartedAt,
            completedAt = completedAt,
            cancelledAt = cancelledAt,
            note = note,
            createdByUserId = createdByUserId,
            updatedByUserId = updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}