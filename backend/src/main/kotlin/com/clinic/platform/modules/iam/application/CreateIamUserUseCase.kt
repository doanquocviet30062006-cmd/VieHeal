package com.clinic.platform.modules.iam.application

import com.clinic.platform.modules.iam.domain.IamUser
import com.clinic.platform.modules.iam.domain.IamUserRepository
import com.clinic.platform.shared.errors.ResourceAlreadyExistsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateIamUserUseCase(
    private val userRepository: IamUserRepository
) {

    @Transactional
    fun execute(
        externalSubject: String?,
        identityProvider: String?,
        email: String?,
        phone: String?,
        displayName: String
    ): IamUser {

        val normalizedEmail = email
            ?.trim()
            ?.lowercase()
            ?.takeIf { it.isNotBlank() }

        val normalizedProvider = identityProvider
            ?.trim()
            ?.lowercase()
            ?.takeIf { it.isNotBlank() }

        val normalizedSubject = externalSubject
            ?.trim()
            ?.takeIf { it.isNotBlank() }

        if (normalizedEmail != null &&
            userRepository.existsByEmail(normalizedEmail)
        ) {
            throw ResourceAlreadyExistsException(
                "User email already exists: $normalizedEmail"
            )
        }

        if (
            normalizedProvider != null &&
            normalizedSubject != null &&
            userRepository.findByExternalIdentity(
                normalizedProvider,
                normalizedSubject
            ) != null
        ) {
            throw ResourceAlreadyExistsException(
                "External identity already exists"
            )
        }

        val user = IamUser.create(
            externalSubject = normalizedSubject,
            identityProvider = normalizedProvider,
            email = normalizedEmail,
            phone = phone,
            displayName = displayName
        )

        return userRepository.save(user)
    }
}