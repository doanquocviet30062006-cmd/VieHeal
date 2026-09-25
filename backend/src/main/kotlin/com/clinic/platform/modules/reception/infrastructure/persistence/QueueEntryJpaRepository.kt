package com.clinic.platform.modules.reception.infrastructure.persistence

import com.clinic.platform.modules.reception.domain.QueueEntryStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface QueueEntryJpaRepository :
    JpaRepository<QueueEntryJpaEntity, UUID> {

    fun findByOrganizationIdAndAppointmentId(
        organizationId: UUID,
        appointmentId: UUID
    ): QueueEntryJpaEntity?

    fun existsByOrganizationIdAndAppointmentId(
        organizationId: UUID,
        appointmentId: UUID
    ): Boolean

    fun findAllByOrganizationIdAndFacilityIdOrderByCheckedInAtAscIdAsc(
        organizationId: UUID,
        facilityId: UUID
    ): List<QueueEntryJpaEntity>

    fun findAllByOrganizationIdAndFacilityIdAndStatusOrderByCheckedInAtAscIdAsc(
        organizationId: UUID,
        facilityId: UUID,
        status: QueueEntryStatus
    ): List<QueueEntryJpaEntity>
}