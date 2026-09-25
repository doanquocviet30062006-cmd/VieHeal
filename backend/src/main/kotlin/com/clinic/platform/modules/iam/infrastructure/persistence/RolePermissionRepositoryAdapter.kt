package com.clinic.platform.modules.iam.infrastructure.persistence

import com.clinic.platform.modules.iam.domain.RolePermissionRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class RolePermissionRepositoryAdapter(
    private val jpaRepository: RolePermissionJpaRepository
) : RolePermissionRepository {

    override fun findPermissionIdsByRoleIds(
        roleIds: Collection<UUID>
    ): Set<UUID> {

        if (roleIds.isEmpty()) {
            return emptySet()
        }

        return jpaRepository
            .findAllByRoleIds(roleIds)
            .map { it.id.permissionId }
            .toSet()
    }
}