package com.clinic.platform.modules.iam.domain

import java.time.Instant
import java.util.UUID

data class IamUser(
    val id: UUID,
    val externalSubject: String?,
    val identityProvider: String?,
    val email: String?,
    val phone: String?,
    val displayName: String,
    val status: UserStatus,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun create(
            externalSubject: String? = null,
            identityProvider: String? = null,
            email: String? = null,
            phone: String? = null,
            displayName: String
        ): IamUser {

            val normalizedDisplayName = displayName.trim()

            require(normalizedDisplayName.isNotBlank()) {
                "Display name must not be blank"
            }

            val now = Instant.now()

            return IamUser(
                id = UUID.randomUUID(),
                externalSubject = externalSubject
                    ?.trim()
                    ?.takeIf { it.isNotBlank() },

                identityProvider = identityProvider
                    ?.trim()
                    ?.lowercase()
                    ?.takeIf { it.isNotBlank() },

                email = email
                    ?.trim()
                    ?.lowercase()
                    ?.takeIf { it.isNotBlank() },

                phone = phone
                    ?.trim()
                    ?.takeIf { it.isNotBlank() },

                displayName = normalizedDisplayName,
                status = UserStatus.ACTIVE,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}