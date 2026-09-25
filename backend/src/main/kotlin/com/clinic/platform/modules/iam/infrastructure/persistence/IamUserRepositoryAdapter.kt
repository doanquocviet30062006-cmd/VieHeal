package com.clinic.platform.modules.iam.infrastructure.persistence

import com.clinic.platform.modules.iam.domain.IamUser
import com.clinic.platform.modules.iam.domain.IamUserRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class IamUserRepositoryAdapter(
    private val jpaRepository: IamUserJpaRepository
) : IamUserRepository {

    override fun save(user: IamUser): IamUser {
        return jpaRepository
            .save(IamUserJpaEntity.fromDomain(user))
            .toDomain()
    }

    override fun findById(id: UUID): IamUser? {
        return jpaRepository
            .findById(id)
            .orElse(null)
            ?.toDomain()
    }

    override fun findByExternalIdentity(
        identityProvider: String,
        externalSubject: String
    ): IamUser? {

        val normalizedProvider =
            identityProvider.trim().lowercase()

        val normalizedSubject =
            externalSubject.trim()

        return jpaRepository
            .findByIdentityProviderAndExternalSubject(
                normalizedProvider,
                normalizedSubject
            )
            ?.toDomain()
    }

    override fun existsByEmail(email: String): Boolean {
        return jpaRepository.existsByEmail(
            email.trim().lowercase()
        )
    }
}