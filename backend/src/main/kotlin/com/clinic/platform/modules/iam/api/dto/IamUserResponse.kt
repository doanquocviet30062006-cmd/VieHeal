package com.clinic.platform.modules.iam.api.dto

import com.clinic.platform.modules.iam.domain.IamUser
import com.clinic.platform.modules.iam.domain.UserStatus
import java.time.Instant
import java.util.UUID

data class IamUserResponse(
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

        fun fromDomain(user: IamUser): IamUserResponse {
            return IamUserResponse(
                id = user.id,
                externalSubject = user.externalSubject,
                identityProvider = user.identityProvider,
                email = user.email,
                phone = user.phone,
                displayName = user.displayName,
                status = user.status,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
            )
        }
    }
}
