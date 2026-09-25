package com.clinic.platform.modules.iam.application

import com.clinic.platform.modules.iam.domain.IamUser
import com.clinic.platform.modules.iam.domain.OrganizationMembership
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

data class ProvisionOrganizationUserResult(
    val user: IamUser,
    val membership: OrganizationMembership
)

@Service
class ProvisionOrganizationUserUseCase(
    private val createIamUserUseCase: CreateIamUserUseCase,
    private val createOrganizationMembershipUseCase:
        CreateOrganizationMembershipUseCase
) {

    @Transactional
    fun execute(
        organizationId: UUID,
        email: String?,
        phone: String?,
        displayName: String
    ): ProvisionOrganizationUserResult {

        val user =
            createIamUserUseCase.execute(
                externalSubject = null,
                identityProvider = null,
                email = email,
                phone = phone,
                displayName = displayName
            )

        val membership =
            createOrganizationMembershipUseCase.execute(
                organizationId = organizationId,
                userId = user.id
            )

        return ProvisionOrganizationUserResult(
            user = user,
            membership = membership
        )
    }
}