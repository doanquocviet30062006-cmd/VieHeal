package com.clinic.platform.modules.iam.infrastructure.persistence

import com.clinic.platform.modules.iam.domain.MembershipStatus
import com.clinic.platform.modules.iam.domain.OrganizationMembership
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
    name = "organization_memberships",
    schema = "iam"
)
class OrganizationMembershipJpaEntity(

    @Id
    @Column(
        name = "id",
        nullable = false
    )
    var id: UUID,

    @Column(
        name = "user_id",
        nullable = false
    )
    var userId: UUID,

    @Column(
        name = "organization_id",
        nullable = false
    )
    var organizationId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 30
    )
    var status: MembershipStatus,

    @Column(
        name = "joined_at",
        nullable = false
    )
    var joinedAt: Instant,

    @Column(
        name = "ended_at"
    )
    var endedAt: Instant?,

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
        userId = UUID.randomUUID(),
        organizationId = UUID.randomUUID(),
        status = MembershipStatus.ACTIVE,
        joinedAt = Instant.EPOCH,
        endedAt = null,
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH
    )

    fun toDomain(): OrganizationMembership {
        return OrganizationMembership(
            id = id,
            userId = userId,
            organizationId = organizationId,
            status = status,
            joinedAt = joinedAt,
            endedAt = endedAt,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {

        fun fromDomain(
            membership: OrganizationMembership
        ): OrganizationMembershipJpaEntity {

            return OrganizationMembershipJpaEntity(
                id = membership.id,
                userId = membership.userId,
                organizationId = membership.organizationId,
                status = membership.status,
                joinedAt = membership.joinedAt,
                endedAt = membership.endedAt,
                createdAt = membership.createdAt,
                updatedAt = membership.updatedAt
            )
        }
    }
}