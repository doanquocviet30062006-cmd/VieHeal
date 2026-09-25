package com.clinic.platform.modules.iam.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface RolePermissionJpaRepository :
    JpaRepository<RolePermissionJpaEntity, RolePermissionId> {

    @Query(
        """
        select rp
        from RolePermissionJpaEntity rp
        where rp.id.roleId in :roleIds
        """
    )
    fun findAllByRoleIds(
        @Param("roleIds")
        roleIds: Collection<java.util.UUID>
    ): List<RolePermissionJpaEntity>
}