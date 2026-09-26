package com.clinic.platform.modules.encounter.infrastructure.persistence

import com.clinic.platform.modules.encounter.domain.Encounter
import com.clinic.platform.modules.encounter.domain.EncounterRepository
import com.clinic.platform.modules.encounter.domain.EncounterStatus
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class EncounterRepositoryAdapter(
    private val jpaRepository:
        EncounterJpaRepository
) : EncounterRepository {

    override fun save(
        encounter: Encounter
    ): Encounter =
        jpaRepository
            .save(
                encounter.toJpaEntity()
            )
            .toDomain()

    override fun findById(
        id: UUID
    ): Encounter? =
        jpaRepository
            .findById(id)
            .orElse(null)
            ?.toDomain()

    override fun findByOrganizationIdAndAppointmentId(
        organizationId: UUID,
        appointmentId: UUID
    ): Encounter? =
        jpaRepository
            .findByOrganizationIdAndAppointmentId(
                organizationId,
                appointmentId
            )
            ?.toDomain()

    override fun findByOrganizationIdAndQueueEntryId(
        organizationId: UUID,
        queueEntryId: UUID
    ): Encounter? =
        jpaRepository
            .findByOrganizationIdAndQueueEntryId(
                organizationId,
                queueEntryId
            )
            ?.toDomain()

    override fun existsByOrganizationIdAndAppointmentId(
        organizationId: UUID,
        appointmentId: UUID
    ): Boolean =
        jpaRepository
            .existsByOrganizationIdAndAppointmentId(
                organizationId,
                appointmentId
            )

    override fun existsByOrganizationIdAndQueueEntryId(
        organizationId: UUID,
        queueEntryId: UUID
    ): Boolean =
        jpaRepository
            .existsByOrganizationIdAndQueueEntryId(
                organizationId,
                queueEntryId
            )

    override fun findAllByOrganizationIdAndFacilityId(
        organizationId: UUID,
        facilityId: UUID
    ): List<Encounter> =
        jpaRepository
            .findAllByOrganizationIdAndFacilityIdOrderByStartedAtDescIdAsc(
                organizationId,
                facilityId
            )
            .map {
                it.toDomain()
            }

    override fun findAllByOrganizationIdAndFacilityIdAndStatus(
        organizationId: UUID,
        facilityId: UUID,
        status: EncounterStatus
    ): List<Encounter> =
        jpaRepository
            .findAllByOrganizationIdAndFacilityIdAndStatusOrderByStartedAtDescIdAsc(
                organizationId,
                facilityId,
                status
            )
            .map {
                it.toDomain()
            }

    private fun Encounter.toJpaEntity():
        EncounterJpaEntity =
        EncounterJpaEntity(
            id = id,
            organizationId = organizationId,
            facilityId = facilityId,
            appointmentId = appointmentId,
            queueEntryId = queueEntryId,
            patientId = patientId,
            practitionerId = practitionerId,
            status = status,
            startedAt = startedAt,
            completedAt = completedAt,
            cancelledAt = cancelledAt,
            cancellationReason =
                cancellationReason,
            createdByUserId =
                createdByUserId,
            updatedByUserId =
                updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

    private fun EncounterJpaEntity.toDomain():
        Encounter =
        Encounter(
            id = id,
            organizationId = organizationId,
            facilityId = facilityId,
            appointmentId = appointmentId,
            queueEntryId = queueEntryId,
            patientId = patientId,
            practitionerId = practitionerId,
            status = status,
            startedAt = startedAt,
            completedAt = completedAt,
            cancelledAt = cancelledAt,
            cancellationReason =
                cancellationReason,
            createdByUserId =
                createdByUserId,
            updatedByUserId =
                updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}