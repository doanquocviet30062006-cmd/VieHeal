package com.clinic.platform.modules.iam.domain

import java.util.UUID

interface IamUserRepository {

    fun save(user: IamUser): IamUser

    fun findById(id: UUID): IamUser?

    fun findByExternalIdentity(
        identityProvider: String,
        externalSubject: String
    ): IamUser?

    fun existsByEmail(email: String): Boolean
}