package com.clinic.platform.modules.servicecatalog.infrastructure.persistence

import com.clinic.platform.modules.servicecatalog.domain.FacilityServiceStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "facility_services",
    schema = "service_catalog"
)
class FacilityServiceJpaEntity(

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
        name = "service_id",
        nullable = false
    )
    var serviceId: UUID,

    @Column(
        name = "duration_minutes",
        nullable = false
    )
    var durationMinutes: Int,

    @Column(
        name = "price_amount",
        nullable = false,
        precision = 12,
        scale = 2
    )
    var priceAmount: BigDecimal,

    @Column(
        name = "currency_code",
        nullable = false,
        length = 3
    )
    var currencyCode: String,

    @Column(
        name = "booking_enabled",
        nullable = false
    )
    var bookingEnabled: Boolean,

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 20
    )
    var status: FacilityServiceStatus,

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