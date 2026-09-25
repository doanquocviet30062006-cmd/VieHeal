package com.clinic.platform.modules.iam.infrastructure.persistence

import com.clinic.platform.modules.iam.domain.IamUser
import com.clinic.platform.modules.iam.domain.UserStatus
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
    name = "users",
    schema = "iam"
)
class IamUserJpaEntity(

    @Id
    @Column(
        name = "id",
        nullable = false
    )
    var id: UUID,

    @Column(
        name = "external_subject",
        length = 255
    )
    var externalSubject: String?,

    @Column(
        name = "identity_provider",
        length = 50
    )
    var identityProvider: String?,

    @Column(
        name = "email",
        length = 255
    )
    var email: String?,

    @Column(
        name = "phone",
        length = 30
    )
    var phone: String?,

    @Column(
        name = "display_name",
        nullable = false,
        length = 255
    )
    var displayName: String,

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 30
    )
    var status: UserStatus,

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
        externalSubject = null,
        identityProvider = null,
        email = null,
        phone = null,
        displayName = "",
        status = UserStatus.ACTIVE,
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH
    )

    fun toDomain(): IamUser {
        return IamUser(
            id = id,
            externalSubject = externalSubject,
            identityProvider = identityProvider,
            email = email,
            phone = phone,
            displayName = displayName,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {

        fun fromDomain(user: IamUser): IamUserJpaEntity {
            return IamUserJpaEntity(
                id = user.id,
                externalSubject = user.externalSubject,
                identityProvider = user.identityProvider,
                email = user.email,
                phone = user.phone,
                displayName = user.displayName,
                status = user.status,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
            )
        }
    }
}