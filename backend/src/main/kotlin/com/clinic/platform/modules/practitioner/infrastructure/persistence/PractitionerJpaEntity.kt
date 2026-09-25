package com.clinic.platform.modules.practitioner.infrastructure.persistence

import com.clinic.platform.modules.practitioner.domain.PractitionerStatus
import com.clinic.platform.modules.practitioner.domain.PractitionerType
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
    name = "practitioners",
    schema = "practitioner"
)
class PractitionerJpaEntity(

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
        name = "membership_id",
        nullable = false
    )
    var membershipId: UUID,

    @Column(
        name = "practitioner_code",
        nullable = false,
        length = 50
    )
    var practitionerCode: String,

    @Column(
        name = "full_name",
        nullable = false,
        length = 255
    )
    var fullName: String,

    @Enumerated(EnumType.STRING)
    @Column(
        name = "practitioner_type",
        nullable = false,
        length = 30
    )
    var practitionerType: PractitionerType,

    @Column(
        name = "license_number",
        length = 100
    )
    var licenseNumber: String?,

    @Column(
        name = "specialty",
        length = 150
    )
    var specialty: String?,

    @Column(
        name = "phone",
        length = 30
    )
    var phone: String?,

    @Column(
        name = "email",
        length = 255
    )
    var email: String?,

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 30
    )
    var status: PractitionerStatus,

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