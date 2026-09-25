package com.clinic.platform.modules.iam.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface IamRoleJpaRepository :
    JpaRepository<IamRoleJpaEntity, UUID> {

    fun findByCode(
        code: String
    ): IamRoleJpaEntity?
}