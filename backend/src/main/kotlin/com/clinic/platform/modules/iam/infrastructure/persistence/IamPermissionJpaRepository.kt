package com.clinic.platform.modules.iam.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface IamPermissionJpaRepository :
    JpaRepository<IamPermissionJpaEntity, UUID>