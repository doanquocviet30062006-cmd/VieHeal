package com.clinic.platform.modules.iam.infrastructure.persistence

import com.clinic.platform.modules.iam.domain.IamRole
import com.clinic.platform.modules.iam.domain.RoleScope
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
    name = "roles",
    schema = "iam"
)
class IamRoleJpaEntity(

    @Id
    @Column(name = "id", nullable = false)
    var id: UUID,

    @Column(
        name = "code",
        nullable = false,
        length = 100
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

    @Enumerated(EnumType.STRING)
    @Column(
        name = "scope",
        nullable = false,
        length = 30
    )
    var scope: RoleScope,

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
        scope = RoleScope.ORGANIZATION,
        createdAt = Instant.EPOCH
    )

    fun toDomain(): IamRole {
        return IamRole(
            id = id,
            code = code,
            name = name,
            description = description,
            scope = scope,
            createdAt = createdAt
        )
    }
}