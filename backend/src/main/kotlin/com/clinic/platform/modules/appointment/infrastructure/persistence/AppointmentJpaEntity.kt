package com.clinic.platform.modules.appointment.infrastructure.persistence

import com.clinic.platform.modules.appointment.domain.AppointmentStatus
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
    name = "appointments",
    schema = "appointment"
)
class AppointmentJpaEntity(

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
        name = "patient_id",
        nullable = false
    )
    var patientId: UUID,

    @Column(
        name = "practitioner_id",
        nullable = false
    )
    var practitionerId: UUID,

    @Column(
        name = "facility_service_id",
        nullable = false
    )
    var facilityServiceId: UUID,

    @Column(
        name = "scheduled_start_at",
        nullable = false
    )
    var scheduledStartAt: Instant,

    @Column(
        name = "scheduled_end_at",
        nullable = false
    )
    var scheduledEndAt: Instant,

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 30
    )
    var status: AppointmentStatus,

    @Column(
        name = "reason",
        length = 1000
    )
    var reason: String?,

    @Column(
        name = "cancellation_reason",
        length = 500
    )
    var cancellationReason: String?,

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