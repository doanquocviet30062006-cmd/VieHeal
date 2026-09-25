package com.clinic.platform.modules.iam.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface IamUserJpaRepository :
    JpaRepository<IamUserJpaEntity, UUID> {

    fun findByIdentityProviderAndExternalSubject(
        identityProvider: String,
        externalSubject: String
    ): IamUserJpaEntity?

    fun existsByEmail(
        email: String
    ): Boolean
}