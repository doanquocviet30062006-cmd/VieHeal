package com.clinic.platform.modules.iam.infrastructure.persistence

import com.clinic.platform.modules.iam.domain.IamRole
import com.clinic.platform.modules.iam.domain.IamRoleRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class IamRoleRepositoryAdapter(
    private val jpaRepository: IamRoleJpaRepository
) : IamRoleRepository {

    override fun findById(id: UUID): IamRole? {
        return jpaRepository
            .findById(id)
            .orElse(null)
            ?.toDomain()
    }

    override fun findByCode(code: String): IamRole? {
        return jpaRepository
            .findByCode(
                code.trim().uppercase()
            )
            ?.toDomain()
    }
}