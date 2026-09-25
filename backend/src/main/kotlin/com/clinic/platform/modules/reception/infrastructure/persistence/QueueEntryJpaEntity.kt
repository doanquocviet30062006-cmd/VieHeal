package com.clinic.platform.modules.reception.infrastructure.persistence

import com.clinic.platform.modules.reception.domain.QueueEntryStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "queue_entries",
    schema = "reception"
)
class QueueEntryJpaEntity(

    @Id
    @Column(
        name = "id",
        nullable = false
    )
    var id: UUID,

    @Column(
        name = "organization_id",
        nullable = false
    )
    var organizationId: UUID,

    @Column(
        name = "facility_id",
        nullable = false
    )
    var facilityId: UUID,

    @Column(
        name = "appointment_id",
        nullable = false
    )
    var appointmentId: UUID,

    @Column(
        name = "patient_id",
        nullable = false
    )
    var patientId: UUID,

    @Column(
        name = "practitioner_id",
        nullable = false
    )
    var practitionerId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 20
    )
    var status: QueueEntryStatus,

    @Column(
        name = "checked_in_at",
        nullable = false
    )
    var checkedInAt: Instant,

    @Column(
        name = "called_at"
    )
    var calledAt: Instant?,

    @Column(
        name = "serving_started_at"
    )
    var servingStartedAt: Instant?,

    @Column(
        name = "completed_at"
    )
    var completedAt: Instant?,

    @Column(
        name = "cancelled_at"
    )
    var cancelledAt: Instant?,

    @Column(
        name = "note",
        length = 1000
    )
    var note: String?,

    @Column(
        name = "created_by_user_id",
        nullable = false
    )
    var createdByUserId: UUID,

    @Column(
        name = "updated_by_user_id",
        nullable = false
    )
    var updatedByUserId: UUID,

    @Column(
        name = "created_at",
        nullable = false
    )
    var createdAt: Instant,

    @Column(
        name = "updated_at",
        nullable = false
    )
    var updatedAt: Instant
)