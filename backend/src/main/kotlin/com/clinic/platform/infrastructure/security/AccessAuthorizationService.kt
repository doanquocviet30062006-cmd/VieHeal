package com.clinic.platform.infrastructure.security

import com.clinic.platform.modules.iam.domain.MembershipStatus
import com.clinic.platform.modules.iam.domain.OrganizationMembershipRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
class AccessAuthorizationService(
    private val currentAccessContextResolver: CurrentAccessContextResolver,
    private val organizationMembershipRepository: OrganizationMembershipRepository
) {

    @Transactional(readOnly = true)
    fun requireOrganizationPermission(
        jwt: Jwt,
        organizationId: UUID,
        permission: String
    ): OrganizationAccess {

        val context =
            currentAccessContextResolver.resolve(jwt)

        val organizationAccess =
            context.organizations.firstOrNull {
                it.organizationId == organizationId
            }
                ?: throw AccessDeniedException(
                    "Organization access denied"
                )

        if (permission !in organizationAccess.permissions) {
            throw AccessDeniedException(
                "Missing permission: $permission"
            )
        }

        return organizationAccess
    }

    @Transactional(readOnly = true)
    fun requireFacilityPermission(
        jwt: Jwt,
        facilityId: UUID,
        permission: String
    ): OrganizationAccess {

        val context =
            currentAccessContextResolver.resolve(jwt)

        val organizationAccess =
            context.organizations.firstOrNull {
                facilityId in it.facilityIds
            }
                ?: throw AccessDeniedException(
                    "Facility access denied"
                )

        if (permission !in organizationAccess.permissions) {
            throw AccessDeniedException(
                "Missing permission: $permission"
            )
        }

        return organizationAccess
    }

    @Transactional(readOnly = true)
    fun requireMembershipPermission(
        jwt: Jwt,
        membershipId: UUID,
        permission: String
    ): OrganizationAccess {

        val targetMembership =
            organizationMembershipRepository
                .findById(membershipId)
                ?.takeIf {
                    it.status == MembershipStatus.ACTIVE
                }
                ?: throw AccessDeniedException(
                    "Membership access denied"
                )

        return requireOrganizationPermission(
            jwt = jwt,
            organizationId = targetMembership.organizationId,
            permission = permission
        )
    }

    @Transactional(readOnly = true)
    fun requireUserPermission(
        jwt: Jwt,
        targetUserId: UUID,
        permission: String
    ): OrganizationAccess {

        val targetOrganizationIds =
            organizationMembershipRepository
                .findAllByUserId(targetUserId)
                .asSequence()
                .filter {
                    it.status == MembershipStatus.ACTIVE
                }
                .map {
                    it.organizationId
                }
                .toSet()

        if (targetOrganizationIds.isEmpty()) {
            throw AccessDeniedException(
                "User access denied"
            )
        }

        val context =
            currentAccessContextResolver.resolve(jwt)

        return context.organizations.firstOrNull {
            it.organizationId in targetOrganizationIds &&
                permission in it.permissions
        }
            ?: throw AccessDeniedException(
                "User access denied"
            )
    }
}