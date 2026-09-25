package com.clinic.platform.modules.iam.infrastructure.persistence

import com.clinic.platform.modules.iam.domain.FacilityAssignment
import com.clinic.platform.modules.iam.domain.FacilityAssignmentStatus
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
    name = "facility_assignments",
    schema = "iam"
)
class FacilityAssignmentJpaEntity(

    @Id
    @Column(
        name = "id",
        nullable = false
    )
    var id: UUID,

    @Column(
        name = "membership_id",
        nullable = false
    )
    var membershipId: UUID,

    @Column(
        name = "facility_id",
        nullable = false
    )
    var facilityId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 30
    )
    var status: FacilityAssignmentStatus,

    @Column(
        name = "assigned_at",
        nullable = false
    )
    var assignedAt: Instant,

    @Column(
        name = "ended_at"
    )
    var endedAt: Instant?

) {

    protected constructor() : this(
        id = UUID.randomUUID(),
        membershipId = UUID.randomUUID(),
        facilityId = UUID.randomUUID(),
        status = FacilityAssignmentStatus.ACTIVE,
        assignedAt = Instant.EPOCH,
        endedAt = null
    )

    fun toDomain(): FacilityAssignment {
        return FacilityAssignment(
            id = id,
            membershipId = membershipId,
            facilityId = facilityId,
            status = status,
            assignedAt = assignedAt,
            endedAt = endedAt
        )
    }

    companion object {

        fun fromDomain(
            assignment: FacilityAssignment
        ): FacilityAssignmentJpaEntity {

            return FacilityAssignmentJpaEntity(
                id = assignment.id,
                membershipId = assignment.membershipId,
                facilityId = assignment.facilityId,
                status = assignment.status,
                assignedAt = assignment.assignedAt,
                endedAt = assignment.endedAt
            )
        }
    }
}