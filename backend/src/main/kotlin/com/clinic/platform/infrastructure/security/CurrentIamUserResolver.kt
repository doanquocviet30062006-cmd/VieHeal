package com.clinic.platform.infrastructure.security

import com.clinic.platform.modules.iam.domain.IamUser
import com.clinic.platform.modules.iam.domain.IamUserRepository
import com.clinic.platform.modules.iam.domain.UserStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CurrentIamUserResolver(
    private val iamUserRepository: IamUserRepository
) {

    @Transactional(readOnly = true)
    fun resolve(jwt: Jwt): IamUser {

        val subject = jwt.subject
            ?: throw AccessDeniedException(
                "JWT subject is missing"
            )

        val user =
            iamUserRepository.findByExternalIdentity(
                identityProvider = "keycloak",
                externalSubject = subject
            )
                ?: throw AccessDeniedException(
                    "Authenticated identity is not linked to an IAM user"
                )

        if (user.status != UserStatus.ACTIVE) {
            throw AccessDeniedException(
                "IAM user is not active"
            )
        }

        return user
    }
}