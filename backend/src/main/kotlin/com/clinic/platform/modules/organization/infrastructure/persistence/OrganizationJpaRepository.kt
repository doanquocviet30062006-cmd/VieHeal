package com.clinic.platform.modules.organization.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OrganizationJpaRepository :
    JpaRepository<OrganizationJpaEntity, UUID> {

    fun existsByCode(code: String): Boolean
}