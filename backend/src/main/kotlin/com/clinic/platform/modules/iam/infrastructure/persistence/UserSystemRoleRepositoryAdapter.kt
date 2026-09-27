package com.clinic.platform.modules.iam.infrastructure.persistence

import com.clinic.platform.modules.iam.domain.UserSystemRoleRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class UserSystemRoleRepositoryAdapter(
    private val jpaRepository: UserSystemRoleJpaRepository
) : UserSystemRoleRepository {

    override fun findRoleIdsByUserId(
        userId: UUID
    ): List<UUID> {
        return jpaRepository
            .findAllByIdUserId(userId)
            .map { it.id.roleId }
    }
}