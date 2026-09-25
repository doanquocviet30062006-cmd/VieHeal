package com.clinic.platform.modules.iam.application

import com.clinic.platform.modules.iam.domain.IamRole
import com.clinic.platform.modules.iam.domain.IamRoleRepository
import com.clinic.platform.modules.iam.domain.MembershipRoleRepository
import com.clinic.platform.modules.iam.domain.OrganizationMembershipRepository
import com.clinic.platform.modules.iam.domain.RoleScope
import com.clinic.platform.shared.errors.ResourceAlreadyExistsException
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AssignMembershipRoleUseCase(
    private val membershipRepository: OrganizationMembershipRepository,
    private val roleRepository: IamRoleRepository,
    private val membershipRoleRepository: MembershipRoleRepository
) {

    @Transactional
    fun execute(
        membershipId: UUID,
        roleCode: String
    ): IamRole {

        membershipRepository.findById(membershipId)
            ?: throw ResourceNotFoundException(
                "Organization membership not found: $membershipId"
            )

        val normalizedRoleCode =
            roleCode.trim().uppercase()

        val role = roleRepository.findByCode(
            normalizedRoleCode
        ) ?: throw ResourceNotFoundException(
            "Role not found: $normalizedRoleCode"
        )

        if (role.scope != RoleScope.ORGANIZATION) {
            throw ResourceNotFoundException(
                "Organization role not found: $normalizedRoleCode"
            )
        }

        if (
            membershipRoleRepository.exists(
                membershipId,
                role.id
            )
        ) {
            throw ResourceAlreadyExistsException(
                "Role is already assigned to membership: $normalizedRoleCode"
            )
        }

        membershipRoleRepository.assign(
            membershipId = membershipId,
            roleId = role.id
        )

        return role
    }
}