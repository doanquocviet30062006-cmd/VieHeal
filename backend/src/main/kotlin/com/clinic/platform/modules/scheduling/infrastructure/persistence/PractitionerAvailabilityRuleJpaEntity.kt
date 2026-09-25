package com.clinic.platform.modules.scheduling.infrastructure.persistence

import com.clinic.platform.modules.scheduling.domain.AvailabilityRuleStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@Entity
@Table(
    name = "practitioner_availability_rules",
    schema = "scheduling"
)
class PractitionerAvailabilityRuleJpaEntity(

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

    @Column(
        name = "day_of_week",
        nullable = false
    )
    var dayOfWeek: Short,

    @Column(
        name = "start_local_time",
        nullable = false
    )
    var startLocalTime: LocalTime,

    @Column(
        name = "end_local_time",
        nullable = false
    )
    var endLocalTime: LocalTime,

    @Column(
        name = "effective_from",
        nullable = false
    )
    var effectiveFrom: LocalDate,

    @Column(
        name = "effective_to"
    )
    var effectiveTo: LocalDate?,

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 30
    )
    var status: AvailabilityRuleStatus,

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