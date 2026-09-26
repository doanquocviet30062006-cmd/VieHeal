package com.clinic.platform.modules.encounter.infrastructure.persistence

import com.clinic.platform.modules.encounter.domain.EncounterStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface EncounterJpaRepository :
    JpaRepository<EncounterJpaEntity, UUID> {

    fun findByOrganizationIdAndAppointmentId(
        organizationId: UUID,
        appointmentId: UUID
    ): EncounterJpaEntity?

    fun findByOrganizationIdAndQueueEntryId(
        organizationId: UUID,
        queueEntryId: UUID
    ): EncounterJpaEntity?

    fun existsByOrganizationIdAndAppointmentId(
        organizationId: UUID,
        appointmentId: UUID
    ): Boolean

    fun existsByOrganizationIdAndQueueEntryId(
        organizationId: UUID,
        queueEntryId: UUID
    ): Boolean

    fun findAllByOrganizationIdAndFacilityIdOrderByStartedAtDescIdAsc(
        organizationId: UUID,
        facilityId: UUID
    ): List<EncounterJpaEntity>

    fun findAllByOrganizationIdAndFacilityIdAndStatusOrderByStartedAtDescIdAsc(
        organizationId: UUID,
        facilityId: UUID,
        status: EncounterStatus
    ): List<EncounterJpaEntity>
}