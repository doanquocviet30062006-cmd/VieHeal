package com.clinic.platform.modules.iam.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserSystemRoleJpaRepository :
    JpaRepository<UserSystemRoleJpaEntity, UserSystemRoleId> {

    fun findAllByIdUserId(
        userId: UUID
    ): List<UserSystemRoleJpaEntity>
}