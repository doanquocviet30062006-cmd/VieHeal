package com.clinic.platform.modules.servicecatalog.infrastructure.persistence

import com.clinic.platform.modules.servicecatalog.domain.ServiceStatus
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
    name = "services",
    schema = "service_catalog"
)
class MedicalServiceJpaEntity(

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
        name = "service_code",
        nullable = false,
        length = 50
    )
    var serviceCode: String,

    @Column(
        name = "name",
        nullable = false,
        length = 255
    )
    var name: String,

    @Column(
        name = "description",
        length = 1000
    )
    var description: String?,

    @Column(
        name = "category",
        length = 100
    )
    var category: String?,

    @Column(
        name = "default_duration_minutes",
        nullable = false
    )
    var defaultDurationMinutes: Int,

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 20
    )
    var status: ServiceStatus,

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