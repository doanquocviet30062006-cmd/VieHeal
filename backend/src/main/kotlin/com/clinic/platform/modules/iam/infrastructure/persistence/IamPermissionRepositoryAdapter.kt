package com.clinic.platform.modules.iam.infrastructure.persistence

import com.clinic.platform.modules.iam.domain.IamPermission
import com.clinic.platform.modules.iam.domain.IamPermissionRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class IamPermissionRepositoryAdapter(
    private val jpaRepository: IamPermissionJpaRepository
) : IamPermissionRepository {

    override fun findById(
        id: UUID
    ): IamPermission? {

        return jpaRepository
            .findById(id)
            .orElse(null)
            ?.toDomain()
    }

    override fun findAllByIds(
        ids: Collection<UUID>
    ): List<IamPermission> {

        if (ids.isEmpty()) {
            return emptyList()
        }

        return jpaRepository
            .findAllById(ids)
            .map { it.toDomain() }
    }
}