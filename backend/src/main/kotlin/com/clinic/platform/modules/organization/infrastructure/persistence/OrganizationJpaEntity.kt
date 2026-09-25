package com.clinic.platform.modules.organization.infrastructure.persistence

import com.clinic.platform.modules.organization.domain.Organization
import com.clinic.platform.modules.organization.domain.OrganizationStatus
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
    name = "organizations",
    schema = "organization"
)
class OrganizationJpaEntity(

    @Id
    @Column(
        name = "id",
        nullable = false
    )
    var id: UUID,

    @Column(
        name = "code",
        nullable = false,
        length = 50
    )
    var code: String,

    @Column(
        name = "name",
        nullable = false,
        length = 255
    )
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 30
    )
    var status: OrganizationStatus,

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
) {

    protected constructor() : this(
        id = UUID.randomUUID(),
        code = "",
        name = "",
        status = OrganizationStatus.ACTIVE,
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH
    )

    fun toDomain(): Organization =
        Organization(
            id = id,
            code = code,
            name = name,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

    companion object {

        fun fromDomain(
            organization: Organization
        ): OrganizationJpaEntity =
            OrganizationJpaEntity(
                id = organization.id,
                code = organization.code,
                name = organization.name,
                status = organization.status,
                createdAt = organization.createdAt,
                updatedAt = organization.updatedAt
            )
    }
}