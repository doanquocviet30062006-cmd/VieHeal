package com.clinic.platform.modules.scheduling.infrastructure.persistence

import com.clinic.platform.modules.scheduling.domain.AvailabilityExceptionStatus
import com.clinic.platform.modules.scheduling.domain.AvailabilityExceptionType
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
    name = "practitioner_availability_exceptions",
    schema = "scheduling"
)
class PractitionerAvailabilityExceptionJpaEntity(

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
        name = "practitioner_id",
        nullable = false
    )
    var practitionerId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(
        name = "exception_type",
        nullable = false,
        length = 30
    )
    var exceptionType: AvailabilityExceptionType,

    @Column(
        name = "start_at",
        nullable = false
    )
    var startAt: Instant,

    @Column(
        name = "end_at",
        nullable = false
    )
    var endAt: Instant,

    @Column(
        name = "reason",
        length = 500
    )
    var reason: String?,

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 30
    )
    var status: AvailabilityExceptionStatus,

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