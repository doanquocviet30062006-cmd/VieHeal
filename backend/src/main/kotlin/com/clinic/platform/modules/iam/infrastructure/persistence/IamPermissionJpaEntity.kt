package com.clinic.platform.modules.iam.infrastructure.persistence

import com.clinic.platform.modules.iam.domain.IamPermission
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "permissions",
    schema = "iam"
)
class IamPermissionJpaEntity(

    @Id
    @Column(
        name = "id",
        nullable = false
    )
    var id: UUID,

    @Column(
        name = "code",
        nullable = false,
        length = 150
    )
    var code: String,

    @Column(
        name = "name",
        nullable = false,
        length = 255
    )
    var name: String,

    @Column(
        name = "description",
        length = 500
    )
    var description: String?,

    @Column(
        name = "created_at",
        nullable = false
    )
    var createdAt: Instant

) {

    protected constructor() : this(
        id = UUID.randomUUID(),
        code = "",
        name = "",
        description = null,
        createdAt = Instant.EPOCH
    )

    fun toDomain(): IamPermission {
        return IamPermission(
            id = id,
            code = code,
            name = name,
            description = description,
            createdAt = createdAt
        )
    }
}